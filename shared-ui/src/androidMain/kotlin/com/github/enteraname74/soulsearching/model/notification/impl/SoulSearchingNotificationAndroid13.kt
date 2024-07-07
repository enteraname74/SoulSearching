package com.github.soulsearching.model.notification.impl

import android.content.Context
import android.support.v4.media.session.MediaSessionCompat.Token
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
    override fun update(isPlaying: Boolean) {
        notificationBuilder
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSessionToken)
            )
        _notification = notificationBuilder.build()

        notificationManager.notify(CHANNEL_ID, _notification)
    }
}