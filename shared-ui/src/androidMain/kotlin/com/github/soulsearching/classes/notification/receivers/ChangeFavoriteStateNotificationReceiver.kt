package com.github.soulsearching.classes.notification.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.soulsearching.classes.player.SoulSearchingAndroidPlayerImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Receiver for changing the favorite state of the current played song.
 */
class ChangeFavoriteStateNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        CoroutineScope(Dispatchers.IO).launch {
            val intentForNotification = Intent(SoulSearchingAndroidPlayerImpl.BROADCAST_NOTIFICATION)
            intentForNotification.putExtra("FAVORITE_CHANGED", true)
            context.sendBroadcast(intentForNotification)
        }
    }
}