package com.github.soulsearching.model.notification.notificationImpl

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.media.session.MediaSessionCompat.Token
import android.util.Log
import com.github.soulsearching.model.notification.SoulSearchingNotification

/**
 * Specification of a SoulSearchingNotification for Android 13 and above.
 */
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