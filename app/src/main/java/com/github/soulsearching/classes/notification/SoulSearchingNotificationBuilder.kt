package com.github.soulsearching.classes.notification

import android.content.Context
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import com.github.soulsearching.classes.notification.notificationImpl.SoulSearchingNotificationAndroid13
import com.github.soulsearching.classes.notification.notificationImpl.SoulSearchingNotificationBelowAndroid13

/**
 * Utils for building the playback notification.
 */
object SoulSearchingNotificationBuilder {

    /**
     * Build a SoulSearchingNotification depending on the device's SDK.
     */
    fun buildNotification(
        context: Context,
        mediaSessionToken: MediaSessionCompat.Token
    ) : SoulSearchingNotification {
        return if (Build.VERSION.SDK_INT >= 33) {
            SoulSearchingNotificationAndroid13(context, mediaSessionToken)
        } else {
            SoulSearchingNotificationBelowAndroid13(context, mediaSessionToken)
        }
    }
}