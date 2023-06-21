package com.github.soulsearching.service

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.AudioManager
import android.os.IBinder
import android.util.Log
import androidx.media3.common.*
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionResult
import androidx.media3.ui.PlayerNotificationManager
import com.github.soulsearching.R
import com.github.soulsearching.classes.PlayerUtils
import com.github.soulsearching.database.model.Music
import com.github.soulsearching.service.notification.MusicNotificationService
import java.io.ByteArrayOutputStream
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
                            addNextMusic()
                            PlayerUtils.playerViewModel.updateCurrentMusicFromUUID(
                                UUID.fromString(player.currentMediaItem!!.mediaId)
                            )
                        }
                        Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED -> {
                            player.play()
                        }
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

        playMusic()
        PlayerUtils.playerViewModel.isPlaying = true

        playerNotificationManager = PlayerNotificationManager.Builder(
            this,
            1,
            MusicNotificationService.MUSIC_NOTIFICATION_CHANNEL_ID
        ).build()

        playerNotificationManager.setPlayer(player)
        playerNotificationManager.setSmallIcon(R.drawable.ic_saxophone_svg)

        session = MediaSession.Builder(applicationContext, player).setCallback(
            object : MediaSession.Callback {
                override fun onPlayerCommandRequest(
                    session: MediaSession,
                    controller: MediaSession.ControllerInfo,
                    playerCommand: Int
                ): Int {
                    Log.d("PLAYER SERVICE", "CATCH COMMAND :$playerCommand")
                    return when(playerCommand) {
                        Player.COMMAND_CHANGE_MEDIA_ITEMS -> {
                            player.play()
                            SessionResult.RESULT_ERROR_PERMISSION_DENIED
                        }
                        Player.COMMAND_SEEK_TO_PREVIOUS -> {
                            addPreviousMusic()
                            PlayerUtils.playerViewModel.updateCurrentMusicFromUUID(
                                UUID.fromString(player.currentMediaItem!!.mediaId)
                            )
                            SessionResult.RESULT_SUCCESS
                        }
                        Player.COMMAND_SEEK_TO_NEXT -> {
                            PlayerUtils.playerViewModel.setNextMusic()
                            addNextMusic()
                            PlayerUtils.playerViewModel.updateCurrentMusicFromUUID(
                                UUID.fromString(player.currentMediaItem!!.mediaId)
                            )
                            SessionResult.RESULT_SUCCESS
                        }
                        Player.COMMAND_SEEK_FORWARD -> {
                            Log.d("PLAYER SERVICE", "CATCH COMMAND : SEEK FORWARD")

                        }
                        else -> super.onPlayerCommandRequest(session, controller, playerCommand)
                    }
                }
            }
        ).build()

        playerNotificationManager.setMediaSessionToken(session.sessionCompatToken)

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
        lateinit var player: ExoPlayer
        lateinit var session: MediaSession

        fun setPlayerPlaylist() {
            player.addMediaItem(mediaItemBuilder(PlayerUtils.playerViewModel.currentMusic!!))
            player.addMediaItem(mediaItemBuilder(PlayerUtils.playerViewModel.getNextMusic()))
            player.prepare()
            setRepeatMode(Player.REPEAT_MODE_ALL)
        }

        fun pauseMusic() {
            player.pause()
        }

        fun addPreviousMusic() {
            val previousMusic = PlayerUtils.playerViewModel.getPreviousMusic()

            player.addMediaItem(0,
                MediaItem.Builder()
                    .setUri(previousMusic.path)
                    .setMediaId(previousMusic.musicId.toString())
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setArtist(previousMusic.artist)
                            .setTitle(previousMusic.name)
                            .setArtworkData(
                                PlayerUtils.playerViewModel.retrieveCoverMethod(previousMusic.coverId)?.let {bitmap ->
                                    val stream = ByteArrayOutputStream()
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                                    return@let stream.toByteArray()
                                },
                                MediaMetadata.PICTURE_TYPE_FRONT_COVER
                            )
                            .build()
                    ).build()
            )
        }

        fun addNextMusic() {
            player.addMediaItem(mediaItemBuilder(PlayerUtils.playerViewModel.getNextMusic()))
        }

        fun playMusic() {
            Log.d("Player Service", "Play music : ${player.currentMediaItem?.mediaId}")
            player.play()
        }

        fun playNext() {
            Log.d("Player Service", "Play music : ${player.currentMediaItem?.mediaId}")
            PlayerUtils.playerViewModel.setNextMusic()
            addNextMusic()
            PlayerUtils.playerViewModel.updateCurrentMusicFromUUID(
                UUID.fromString(player.currentMediaItem!!.mediaId)
            )
            player.seekToNext()
        }

        fun playPrevious() {
            addPreviousMusic()
            PlayerUtils.playerViewModel.updateCurrentMusicFromUUID(
                UUID.fromString(player.currentMediaItem!!.mediaId)
            )
            player.seekTo(0,0)
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
            PlayerUtils.playerViewModel.resetPlayerData()
            player
        }

        private fun mediaItemBuilder(music: Music) : MediaItem {
            return MediaItem.Builder()
                .setUri(music.path)
                .setMediaId(music.musicId.toString())
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setArtist(music.artist)
                        .setTitle(music.name)
                        .setArtworkData(
                            PlayerUtils.playerViewModel.retrieveCoverMethod(music.coverId)?.let {bitmap ->
                                val stream = ByteArrayOutputStream()
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                                return@let stream.toByteArray()
                            },
                            MediaMetadata.PICTURE_TYPE_FRONT_COVER
                        )
                        .build()
                ).build()
        }
    }
}