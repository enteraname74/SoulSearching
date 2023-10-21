package com.github.soulsearching.classes.notification.notificationImpl

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.media.session.MediaSessionCompat.Token
import android.util.Log
import com.github.soulsearching.R
import com.github.soulsearching.classes.PlayerUtils
import com.github.soulsearching.classes.notification.SoulSearchingNotification
import com.github.soulsearching.classes.player.SoulSearchingMediaPlayerImpl

class SoulSearchingNotificationAndroid13(
    context: Context,
    mediaSessionToken: Token
) : SoulSearchingNotification(
    context,
    mediaSessionToken
) {

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

    override fun initializeNotification() {
        context.registerReceiver(
            broadcastReceiver,
            IntentFilter(SoulSearchingMediaPlayerImpl.BROADCAST_NOTIFICATION)
        )

        notificationBuilder
            .setSmallIcon(R.drawable.ic_saxophone_svg)
            .setContentTitle(if (PlayerUtils.playerViewModel.currentMusic != null) PlayerUtils.playerViewModel.currentMusic?.name else "")
            .setContentText(if (PlayerUtils.playerViewModel.currentMusic != null) PlayerUtils.playerViewModel.currentMusic?.artist else "")
            .setContentIntent(activityPendingIntent)
            .setDeleteIntent(deleteNotificationIntent)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSessionToken)
            )

        notification = notificationBuilder.build()
    }

    override fun updateNotification() {
        notificationBuilder
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSessionToken)
            )
        notification = notificationBuilder.build()

        notificationManager.notify(CHANNEL_ID, notification)
    }
}