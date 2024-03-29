package com.github.soulsearching.classes.notification.notificationImpl

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import com.github.soulsearching.R
import com.github.soulsearching.classes.notification.SoulSearchingNotification
import com.github.soulsearching.classes.notification.receivers.NextMusicNotificationReceiver
import com.github.soulsearching.classes.notification.receivers.PausePlayNotificationReceiver
import com.github.soulsearching.classes.notification.receivers.PreviousMusicNotificationReceiver
import com.github.soulsearching.service.PlayerService

/**
 * Specification of a SoulSearchingNotification for devices below Android 13.
 */
class SoulSearchingNotificationBelowAndroid13(
    context: Context,
    mediaSessionToken: MediaSessionCompat.Token
) : SoulSearchingNotification(
    context,
    mediaSessionToken
) {

    private val previousMusicIntent: PendingIntent = PendingIntent.getBroadcast(
        context,
        2,
        Intent(context, PreviousMusicNotificationReceiver::class.java),
        PendingIntent.FLAG_IMMUTABLE
    )

    private val pausePlayIntent: PendingIntent = PendingIntent.getBroadcast(
        context,
        3,
        Intent(context, PausePlayNotificationReceiver::class.java),
        PendingIntent.FLAG_IMMUTABLE
    )

    private val nextMusicIntent: PendingIntent = PendingIntent.getBroadcast(
        context,
        4,
        Intent(context, NextMusicNotificationReceiver::class.java),
        PendingIntent.FLAG_IMMUTABLE
    )

    override val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("RECEIVE", "RECEIVE")
            if (intent.extras?.getBoolean("STOP_RECEIVE") != null && intent.extras?.getBoolean("STOP_RECEIVE") as Boolean) {
                context.unregisterReceiver(this)
//                PlayerService.stopMusic(context)
            } else {
                updateNotification()
            }
        }
    }

    override fun updateNotification() {
        val pausePlayIcon = if (PlayerService.isPlayerPlaying()) {
            R.drawable.ic_pause
        } else {
            R.drawable.ic_play_arrow
        }

        notificationBuilder
            .clearActions()
            .addAction(R.drawable.ic_skip_previous,"previous",previousMusicIntent)
            .addAction(pausePlayIcon,"pausePlay",pausePlayIntent)
            .addAction(R.drawable.ic_skip_next,"next",nextMusicIntent)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0,1,2)
                    .setMediaSession(mediaSessionToken)
            )
        notification = notificationBuilder.build()

        notificationManager.notify(CHANNEL_ID, notification)
    }
}