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
import kotlin.collections.ArrayList


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
//                            PlayerUtils.playerViewModel.setNextMusic()
//                            addNextMusic()
//                            PlayerUtils.playerViewModel.updateCurrentMusicFromUUID(
//                                UUID.fromString(player.currentMediaItem!!.mediaId)
//                            )
                            playNext()
                        }
                        Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED -> {}
                        Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT -> {
                            playNext()
                        }
                        Player.MEDIA_ITEM_TRANSITION_REASON_SEEK -> {
                            PlayerUtils.playerViewModel.updateCurrentMusicFromUUID(
                                UUID.fromString(player.currentMediaItem!!.mediaId)
                            )
                        }
                    }
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    Log.d("PLAYBACK STATE CHANGED", playbackState.toString())
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
                    return when (playerCommand) {
                        Player.COMMAND_CHANGE_MEDIA_ITEMS -> {
                            player.play()
                            SessionResult.RESULT_ERROR_PERMISSION_DENIED
                        }
                        Player.COMMAND_SEEK_TO_PREVIOUS -> {
                            playPrevious()
                            SessionResult.RESULT_SUCCESS
                        }
                        Player.COMMAND_SEEK_TO_NEXT -> {
                            playNext()
                            player.seekTo(0,0L)
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

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    override fun onDestroy() {
        Log.d("DESTROY SERVICE", "DESTROY SERVICE")
        super.onDestroy()
        stopForeground(STOP_FOREGROUND_REMOVE)
        val notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1)
    }

    companion object {
        lateinit var audioAttributes: AudioAttributes
        lateinit var audioManager: AudioManager
        lateinit var player: ExoPlayer
        lateinit var session: MediaSession
        var playerList: ArrayList<UUID> = ArrayList()

        fun setPlayerPlaylist() {
            player.stop()
            player.clearMediaItems()
            val currentMusic = PlayerUtils.playerViewModel.currentMusic!!
            val nextMusic = PlayerUtils.playerViewModel.getNextMusic()
            player.addMediaItem(mediaItemBuilder(currentMusic))
            //player.addMediaItem(mediaItemBuilder(nextMusic))
            player.prepare()
            setRepeatMode(Player.REPEAT_MODE_ALL)
        }

        fun pauseMusic() {
            player.pause()
        }

        fun playMusic() {
            player.play()
        }

        fun playNext() {
//            Log.d("PLAYER INDEX :",player.currentMediaItemIndex.toString())
//            Log.d("LAST LIST INDEX :",playerList.lastIndex.toString())
//            if (player.currentMediaItemIndex == playerList.lastIndex || playerList.size == 0) {
//                addNextMusic()
//            }
            PlayerUtils.playerViewModel.setNextMusic()
            player.stop()
            setPlayerPlaylist()
            player.play()
            PlayerUtils.playerViewModel.updateCurrentMusicFromUUID(
                UUID.fromString(player.currentMediaItem!!.mediaId)
            )
        }

        fun playPrevious() {
//            if (player.currentMediaItemIndex == 0) {
//                player.addMediaItem(
//                    player.currentMediaItemIndex,
//                    mediaItemBuilder(PlayerUtils.playerViewModel.getPreviousMusic())
//                )
//            }
            PlayerUtils.playerViewModel.setPreviousMusic()
            player.stop()
            setPlayerPlaylist()
            player.play()
            PlayerUtils.playerViewModel.updateCurrentMusicFromUUID(
                UUID.fromString(player.currentMediaItem!!.mediaId)
            )
//            player.seekTo(player.currentMediaItemIndex - 1, 0L)
        }

        fun seekToCurrentMusic() {
            Log.d("Player Service", "Seek to : ${PlayerUtils.playerViewModel.currentMusic?.name}")
            player.seekTo(
                PlayerUtils.playerViewModel.playlistInfos.indexOf(PlayerUtils.playerViewModel.currentMusic),
                0L
            )
        }

        private fun setRepeatMode(repeatMode: Int) {
            player.repeatMode = repeatMode
        }

        fun stopMusic(context: Context) {
            Log.d("Player Service", "Stop music !")
            player.stop()
            player.release()
            session.release()
            PlayerUtils.playerViewModel.resetPlayerData()
            playerList.clear()
            val serviceIntent = Intent(context, PlayerService::class.java)
            context.stopService(serviceIntent)
        }

        private fun mediaItemBuilder(music: Music): MediaItem {
            return MediaItem.Builder()
                .setUri(music.path)
                .setMediaId(music.musicId.toString())
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setArtist(music.artist)
                        .setTitle(music.name)
                        .setArtworkData(
                            PlayerUtils.playerViewModel.retrieveCoverMethod(music.coverId)
                                ?.let { bitmap ->
                                    val stream = ByteArrayOutputStream()
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                                    return@let stream.toByteArray()
                                },
                            MediaMetadata.PICTURE_TYPE_FRONT_COVER
                        )
                        .build()
                ).build()
        }

        private fun showPlayerList() {
            for (i in 0 until playerList.size) {
                Log.d("ELEMENT", "$i : ${playerList[i]}")
            }
        }
    }
}