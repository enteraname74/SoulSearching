package com.github.enteraname74.soulsearching.features.playback.notification.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.enteraname74.soulsearching.features.playback.notification.impl.SoulSearchingAndroidNotification

/**
 * Receiver for playing or pausing the current played song.
 */
class PausePlayNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val intentForNotification = Intent(SoulSearchingAndroidNotification.BROADCAST_NOTIFICATION)
        intentForNotification.putExtra(SoulSearchingAndroidNotification.TOGGLE_PLAY_PAUSE, true)
        context.sendBroadcast(intentForNotification)
    }
}