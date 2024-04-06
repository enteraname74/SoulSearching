package com.github.soulsearching.model.notification.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.soulsearching.model.PlaybackManager
import com.github.soulsearching.model.playback.PlaybackManagerAndroidImpl
import com.github.soulsearching.model.player.SoulSearchingAndroidPlayerImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Receiver for changing the favorite state of the current played song.
 */
class ChangeFavoriteStateNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        CoroutineScope(Dispatchers.IO).launch {
            val intentForNotification = Intent(PlaybackManagerAndroidImpl.BROADCAST_NOTIFICATION)
            intentForNotification.putExtra("FAVORITE_CHANGED", true)
            context.sendBroadcast(intentForNotification)
        }
    }
}