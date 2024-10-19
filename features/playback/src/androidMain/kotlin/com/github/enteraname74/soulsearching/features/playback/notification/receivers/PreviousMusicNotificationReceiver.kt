package com.github.enteraname74.soulsearching.features.playback.notification.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.enteraname74.soulsearching.features.playback.notification.impl.SoulSearchingAndroidNotification

/**
 * Receiver for playing the previous song in the queue.
 */
class PreviousMusicNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val intentForNotification = Intent(SoulSearchingAndroidNotification.BROADCAST_NOTIFICATION)
        intentForNotification.putExtra(SoulSearchingAndroidNotification.PREVIOUS, true)
        context.sendBroadcast(intentForNotification)
    }
}