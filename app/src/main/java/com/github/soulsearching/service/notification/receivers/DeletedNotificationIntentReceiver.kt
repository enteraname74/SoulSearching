package com.github.soulsearching.service.notification.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeletedNotificationIntentReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        CoroutineScope(Dispatchers.IO).launch {
            val intentForNotification = Intent("BROADCAST_NOTIFICATION")
            intentForNotification.putExtra("STOP_RECEIVE", true)
            context.sendBroadcast(intentForNotification)
        }
    }
}