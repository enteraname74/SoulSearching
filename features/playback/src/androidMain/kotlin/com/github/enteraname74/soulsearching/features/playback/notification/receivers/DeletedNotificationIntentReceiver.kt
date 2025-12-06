package com.github.enteraname74.soulsearching.features.playback.notification.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.enteraname74.soulsearching.features.playback.notification.impl.SoulSearchingAndroidNotification
import com.github.enteraname74.soulsearching.features.playback.notification.impl.SoulSearchingNotificationBelowAndroid13
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Receiver for deleting the notification.
 */
class DeletedNotificationIntentReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        CoroutineScope(Dispatchers.IO).launch {
            val intentForNotification = Intent(SoulSearchingAndroidNotification.BROADCAST_NOTIFICATION)
            intentForNotification.putExtra(SoulSearchingNotificationBelowAndroid13.STOP_RECEIVE, true)
            context.sendBroadcast(intentForNotification)
        }
    }
}