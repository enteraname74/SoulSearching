package com.github.soulsearching.model.notification.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.soulsearching.model.player.SoulSearchingAndroidPlayerImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Receiver for deleting the notification.
 */
class DeletedNotificationIntentReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        CoroutineScope(Dispatchers.IO).launch {
            val intentForNotification = Intent(SoulSearchingAndroidPlayerImpl.BROADCAST_NOTIFICATION)
            intentForNotification.putExtra("STOP_RECEIVE", true)
            context.sendBroadcast(intentForNotification)
        }
    }
}