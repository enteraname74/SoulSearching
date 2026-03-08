package com.github.enteraname74.soulsearching.features.playback.notification.impl

import android.app.Notification
import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import com.github.enteraname74.soulsearching.features.playback.model.UpdateData

/**
 * Specification of a SoulSearchingNotification for Android 13 and above.
 */
class SoulSearchingNotificationAndroid13(
    context: Context,
) : SoulSearchingAndroidNotification(
    context = context,
) {

    override fun provideNotification(
        updateData: UpdateData,
        mediaSessionToken: MediaSessionCompat.Token,
    ): Notification =
        notificationBuilder
            .soulNotificationBuilder(
                updateData = updateData,
                mediaSessionToken = mediaSessionToken,
            )
            .build()
}