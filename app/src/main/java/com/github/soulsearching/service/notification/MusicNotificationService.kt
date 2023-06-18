package com.github.soulsearching.service.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadata
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import com.github.soulsearching.MainActivity
import com.github.soulsearching.R
import com.github.soulsearching.classes.PlayerUtils
import com.github.soulsearching.service.PlayerService
import com.github.soulsearching.service.notification.receivers.*

class MusicNotificationService(private val context : Context) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private lateinit var notificationMusicPlayer : NotificationCompat.Builder
    private lateinit var pausePlayIntent : PendingIntent
    private lateinit var previousMusicIntent : PendingIntent
    private lateinit var nextMusicIntent : PendingIntent
    private lateinit var deleteNotificationIntent : PendingIntent
    private lateinit var changeFavoriteStateIntent : PendingIntent

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.extras?.getBoolean("STOP_RECEIVE") != null && intent.extras?.getBoolean("STOP_RECEIVE") as Boolean) {
                context.unregisterReceiver(this)
            } else {
                updateNotification()
            }
        }
    }
    fun showNotification(){
        createNotificationChannel()

        val bitmap = if (PlayerUtils.playerViewModel.currentMusicCover != null) {
            PlayerUtils.playerViewModel.currentMusicCover
        } else {
            BitmapFactory.decodeResource(context.resources, R.drawable.notification_default)
        }

        context.registerReceiver(broadcastReceiver, IntentFilter("BROADCAST_NOTIFICATION"))

        val activityIntent = Intent(context, MainActivity::class.java)
        activityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        activityIntent.action = Intent.ACTION_MAIN
        activityIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        val pausePlayIcon = if (PlayerService.player.isPlaying) {
            R.drawable.ic_pause
        } else {
            R.drawable.ic_play_arrow
        }

        val activityPendingIntent = PendingIntent.getActivity(
            context,
            1,
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        previousMusicIntent = PendingIntent.getBroadcast(
            context,
            2,
            Intent(context, PreviousMusicNotificationReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        pausePlayIntent = PendingIntent.getBroadcast(
            context,
            3,
            Intent(context, PausePlayNotificationReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        nextMusicIntent = PendingIntent.getBroadcast(
            context,
            4,
            Intent(context, NextMusicNotificationReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        deleteNotificationIntent = PendingIntent.getBroadcast(
            context,
            5,
            Intent(context, DeletedNotificationIntentReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        changeFavoriteStateIntent = PendingIntent.getBroadcast(
            context,
            6,
            Intent(context, ChangeFavoriteStateNotificationReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        notificationMusicPlayer = NotificationCompat.Builder(context, MUSIC_NOTIFICATION_CHANNEL_ID)
            .setLargeIcon(bitmap)
            .setSmallIcon(R.drawable.ic_saxophone_svg)
            .setContentTitle(if (PlayerUtils.playerViewModel.currentMusic != null) PlayerUtils.playerViewModel.currentMusic?.name else "")
            .setContentText(if (PlayerUtils.playerViewModel.currentMusic != null) PlayerUtils.playerViewModel.currentMusic?.artist else "")
            .setContentIntent(activityPendingIntent)
            .addAction(R.drawable.ic_skip_previous,"previous",previousMusicIntent)
            .addAction(pausePlayIcon,"pausePlay",pausePlayIntent)
            .addAction(R.drawable.ic_skip_next,"next",nextMusicIntent)
            .addAction(pausePlayIcon, "favoriteState", changeFavoriteStateIntent)
            .setDeleteIntent(deleteNotificationIntent)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(PlayerService.mediaSession.sessionToken)
                .setShowActionsInCompactView(0, 1, 2)
            )

        notificationManager.notify(
            1,
            notificationMusicPlayer.build()
        )
    }

    private fun updateNotification() {
        val musicState = if (PlayerService.player.isPlaying) {
            PlaybackStateCompat.STATE_PLAYING
        } else {
            PlaybackStateCompat.STATE_PAUSED
        }

        val pausePlayIcon = if (PlayerService.player.isPlaying) {
            R.drawable.ic_pause
        } else {
            R.drawable.ic_play_arrow
        }

        val bitmap = if (PlayerUtils.playerViewModel.currentMusicCover != null) {
            PlayerUtils.playerViewModel.currentMusicCover
        } else {
            BitmapFactory.decodeResource(context.resources, R.drawable.notification_default)
        }

        PlayerService.mediaSession.setMetadata(
            MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, bitmap)
                .putLong(
                    MediaMetadataCompat.METADATA_KEY_DURATION,
                    PlayerService.player.duration
                )
                .putString(
                    MediaMetadata.METADATA_KEY_DISPLAY_TITLE,
                    if (PlayerUtils.playerViewModel.currentMusic != null) {
                        PlayerUtils.playerViewModel.currentMusic!!.name
                    } else {
                        ""
                    }
                )
                .putLong(
                    MediaMetadata.METADATA_KEY_TRACK_NUMBER,
                    PlayerService.player.currentMediaItemIndex.toLong()
                )
                .putLong(
                    MediaMetadata.METADATA_KEY_NUM_TRACKS,
                    PlayerUtils.playerViewModel.playlistInfos.size.toLong()
                )
                // Pour les vieilles versions d'android
                .putString(
                    MediaMetadata.METADATA_KEY_TITLE,
                    if (PlayerUtils.playerViewModel.currentMusic != null) {
                        PlayerUtils.playerViewModel.currentMusic!!.name
                    } else {
                        ""
                    }
                )
                .putString(
                    MediaMetadata.METADATA_KEY_ARTIST,
                    if (PlayerUtils.playerViewModel.currentMusic != null) {
                        PlayerUtils.playerViewModel.currentMusic!!.artist
                    } else {
                        ""
                    }
                )
                .putBitmap(MediaMetadata.METADATA_KEY_ART, bitmap)
                .build()
        )


        val state = PlaybackStateCompat.Builder()
            .setActions(PlaybackStateCompat.ACTION_PLAY
                    or PlaybackStateCompat.ACTION_SEEK_TO
                    or PlaybackStateCompat.ACTION_PAUSE
                    or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    or PlaybackStateCompat.ACTION_PLAY_PAUSE
            )
            .setState(musicState, PlayerService.player.currentMediaItemIndex.toLong(), 1.0F)
            .build()

        PlayerService.mediaSession.setPlaybackState(state)

        notificationMusicPlayer
            .clearActions()
            .addAction(R.drawable.ic_skip_previous,"previous",previousMusicIntent)
            .addAction(pausePlayIcon,"pausePlay",pausePlayIntent)
            .addAction(R.drawable.ic_skip_next,"next",nextMusicIntent)
            .addAction(R.drawable.ic_pause, "favoriteState", changeFavoriteStateIntent)
            .setDeleteIntent(deleteNotificationIntent)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(PlayerService.mediaSession.sessionToken)
                .setShowActionsInCompactView(0, 1, 2)
            )

        notificationManager.notify(1, notificationMusicPlayer.build())
    }

    private fun createNotificationChannel(){
        val channel = NotificationChannel(
            MUSIC_NOTIFICATION_CHANNEL_ID,
            "SoulSearchingNotification",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "Use for controlling the song that is currently playing"

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

    }

    companion object {
        const val MUSIC_NOTIFICATION_CHANNEL_ID = "music_notification_channel"
    }
}