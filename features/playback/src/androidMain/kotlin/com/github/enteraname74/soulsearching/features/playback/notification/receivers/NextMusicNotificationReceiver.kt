package com.github.enteraname74.soulsearching.features.playback.notification.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.enteraname74.soulsearching.features.playback.notification.impl.SoulSearchingAndroidNotification
import com.github.enteraname74.soulsearching.features.playback.notification.impl.SoulSearchingNotificationBelowAndroid13

/**
 * Receiver for playing the next song in the queue.
 */
class NextMusicNotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val intentForNotification = Intent(SoulSearchingAndroidNotification.BROADCAST_NOTIFICATION)
        intentForNotification.putExtra(SoulSearchingNotificationBelowAndroid13.NEXT, true)
        context.sendBroadcast(intentForNotification)
    }
}