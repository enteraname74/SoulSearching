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
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerNotificationManager
import com.github.soulsearching.R
import com.github.soulsearching.classes.PlayerUtils
import com.github.soulsearching.service.notification.MusicNotificationService
import java.util.*

class PlayerService : Service() {
    private lateinit var playerNotificationManager: PlayerNotificationManager
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
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
                        Player.MEDIA_ITEM_TRANSITION_REASON_SEEK -> {
                            PlayerUtils.playerViewModel.updateCurrentMusicFromUUID(
                                UUID.fromString(player.currentMediaItem!!.mediaId)
                            )
                        }
                    }

                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
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
                                PlayerUtils.playerViewModel.setPlayingState(applicationContext)
                            }
                            KeyEvent.KEYCODE_MEDIA_PLAY -> {
                                PlayerUtils.playerViewModel.setPlayingState(applicationContext)
                            }
                            KeyEvent.KEYCODE_MEDIA_NEXT -> {
                                PlayerUtils.playerViewModel.setNextMusic()
                                playNext(applicationContext)
                            }
                            KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                                PlayerUtils.playerViewModel.setPreviousMusic()
                                playPrevious(applicationContext)
                            }
                        }
                    }
                }
                return super.onMediaButtonEvent(mediaButtonIntent)
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                playPrevious(applicationContext)
            }
        })

        mediaSession.setPlaybackState(
            updateMediaSessionState(
                PlaybackStateCompat.STATE_PLAYING,
                PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN
            )
        )
        mediaSession.isActive = true

        seekToCurrentMusic(this)
        playMusic(this)
        PlayerUtils.playerViewModel.isPlaying = true


        playerNotificationManager = PlayerNotificationManager.Builder(
            this,
            1,
            MusicNotificationService.MUSIC_NOTIFICATION_CHANNEL_ID
        ).build()

        playerNotificationManager.setPlayer(player)
        playerNotificationManager.setSmallIcon(R.drawable.ic_saxophone_svg)

        session = MediaSession.Builder(applicationContext, player).build()

        playerNotificationManager.setMediaSessionToken(session.sessionCompatToken)


//        notificationService = MusicNotificationService(applicationContext)
//        notificationService.showNotification()
//        val intentForNotification = Intent("BROADCAST_NOTIFICATION")
//        this.sendBroadcast(intentForNotification)

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
        lateinit var session: MediaSession

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
                    MediaItem.Builder()
                        .setUri(it.path)
                        .setMediaId(it.musicId.toString())
                        .build()
                },
                true
            )
            Log.d("Player Service", "end initialize playlist")
            player.prepare()
            setRepeatMode(Player.REPEAT_MODE_ALL)
            Log.d("Player Service", "end prepare")
        }

        fun pauseMusic(context: Context) {
            player.pause()
//            val intentForNotification = Intent("BROADCAST_NOTIFICATION")
//            context.sendBroadcast(intentForNotification)
        }

        fun playMusic(context: Context) {
            Log.d("Player Service", "Play music : ${player.currentMediaItem?.mediaId}")
            player.play()
//            val intentForNotification = Intent("BROADCAST_NOTIFICATION")
//            context.sendBroadcast(intentForNotification)
        }

        fun playNext(context: Context) {
            Log.d("Player Service", "Play music : ${player.currentMediaItem?.mediaId}")
            player.seekToNext()
//            val intentForNotification = Intent("BROADCAST_NOTIFICATION")
//            context.sendBroadcast(intentForNotification)
        }

        fun playPrevious(context: Context) {
            player.seekToPrevious()
//            val intentForNotification = Intent("BROADCAST_NOTIFICATION")
//            context.sendBroadcast(intentForNotification)
        }

        fun seekToCurrentMusic(context: Context) {
            Log.d("Player Service","Seek to : ${PlayerUtils.playerViewModel.currentMusic?.name}")
            player.seekTo(
                PlayerUtils.playerViewModel.playlistInfos.indexOf(PlayerUtils.playerViewModel.currentMusic),
                0L
            )
//            val intentForNotification = Intent("BROADCAST_NOTIFICATION")
//            context.sendBroadcast(intentForNotification)
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
            PlayerUtils.playerViewModel.resetPlayerData()
        }
    }
}