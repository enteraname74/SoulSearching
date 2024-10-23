package com.github.enteraname74.soulsearching.features.playback.notification.impl

import android.app.Notification
import android.content.Context

/**
 * Specification of a SoulSearchingNotification for Android 13 and above.
 */
class SoulSearchingNotificationAndroid13(
    context: Context,
) : SoulSearchingAndroidNotification(
    context = context,
) {

    override fun provideNotification(state: SoulSearchingAndroidNotificationState.Active): Notification =
        notificationBuilder
            .soulNotificationBuilder(state)
            .build()
}