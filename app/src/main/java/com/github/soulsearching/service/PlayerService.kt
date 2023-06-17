package com.github.soulsearching.service

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.KeyEvent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.github.soulsearching.classes.PlayerUtils

class PlayerService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
    override fun  onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("START COMMAND", "")
        player = ExoPlayer.Builder(this).build()

        player.addListener(
            object : Player.Listener {
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    Log.d("Player Service", "MEDIA ITEM TRANSITION : REASON : $reason")
                    super.onMediaItemTransition(mediaItem, reason)
                    when (reason) {
                        Player.MEDIA_ITEM_TRANSITION_REASON_AUTO -> {
                            PlayerUtils.playerViewModel.setNextMusic()
                        }
                        Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED -> {}
                        Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT -> {}
                        Player.MEDIA_ITEM_TRANSITION_REASON_SEEK -> {}
                    }

                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    Log.d("PLAYER SERVICE", "IS PLAYING ? $isPlaying")
                    PlayerUtils.playerViewModel.isPlaying = isPlaying
                }
            }
        )

        setPlayerPlaylist()

        audioManager = applicationContext.getSystemService(AUDIO_SERVICE) as AudioManager
        audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        player.setAudioAttributes(audioAttributes, true)

        mediaSession = MediaSessionCompat(applicationContext, packageName + "mediaSessionPlayer")

        mediaSession.setCallback(object : MediaSessionCompat.Callback() {
            override fun onSeekTo(pos: Long) {
                player.seekTo(pos)
                val intentForNotification = Intent("BROADCAST_NOTIFICATION")
                intentForNotification.putExtra("STOP", !player.isPlaying)
                applicationContext.sendBroadcast(intentForNotification)
            }

            override fun onMediaButtonEvent(mediaButtonIntent: Intent): Boolean {
                if (PlayerUtils.playerViewModel.currentMusic != null) {
                    val keyEvent = mediaButtonIntent.extras?.get(Intent.EXTRA_KEY_EVENT) as KeyEvent
                    if (keyEvent.action == KeyEvent.ACTION_DOWN){
                        when(keyEvent.keyCode){
                            KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                            }
                            KeyEvent.KEYCODE_MEDIA_PLAY -> {
                            }
                            KeyEvent.KEYCODE_MEDIA_NEXT -> {
                            }
                            KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                            }
                        }
                    }
                }
                return super.onMediaButtonEvent(mediaButtonIntent)
            }
        })

        mediaSession.setPlaybackState(
            updateMediaSessionState(
                PlaybackStateCompat.STATE_PLAYING,
                PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN
            )
        )
        mediaSession.isActive = true

        seekToCurrentMusic()
        playMusic()
        PlayerUtils.playerViewModel.isPlaying = true

        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("ON CREATE", "")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.d("PLAYBACK SERVICE", "REMOVED")
        stopMusic(this)
    }

    companion object {
        lateinit var audioAttributes: AudioAttributes
        lateinit var audioManager: AudioManager
        lateinit var mediaSession: MediaSessionCompat
        lateinit var player: ExoPlayer

        fun updateMediaSessionState(musicState: Int, musicPosition: Long): PlaybackStateCompat {
            return PlaybackStateCompat.Builder()
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY
                            or PlaybackStateCompat.ACTION_SEEK_TO
                            or PlaybackStateCompat.ACTION_PAUSE
                            or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                            or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                            or PlaybackStateCompat.ACTION_PLAY_PAUSE
                )
                .setState(
                    musicState,
                    musicPosition,
                    1.0F
                )
                .build()
        }

        fun setPlayerPlaylist() {
            Log.d("Player Service", "initialize playlist")
            player.setMediaItems(
                PlayerUtils.playerViewModel.playlistInfos.map {
                    MediaItem.Builder().setUri(it.path).setMediaId(it.musicId.toString()).build()
                },
                true
            )
            Log.d("Player Service", "end initialize playlist")
            player.prepare()
            setRepeatMode(Player.REPEAT_MODE_ALL)
            Log.d("Player Service", "end prepare")
        }

        fun pauseMusic() {
            player.pause()
        }

        fun playMusic() {
            Log.d("Player Service", "Play music : ${player.currentMediaItem?.mediaId}")
            player.play()
        }

        fun playNext() {
            Log.d("Player Service", "Play music : ${player.currentMediaItem?.mediaId}")
            player.seekToNext()
        }

        fun playPrevious() {
            player.seekToPrevious()
        }

        fun seekToCurrentMusic() {
            Log.d("Player Service","Seek to : ${PlayerUtils.playerViewModel.currentMusic?.name}")
            player.seekTo(
                PlayerUtils.playerViewModel.playlistInfos.indexOf(PlayerUtils.playerViewModel.currentMusic),
                0L
            )
        }

        fun setRepeatMode(repeatMode : Int) {
            player.repeatMode = repeatMode
        }

        fun stopMusic(context: Context) {
            Log.d("Player Service", "Stop music !")
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(1)
            player.pause()
        }
    }
}