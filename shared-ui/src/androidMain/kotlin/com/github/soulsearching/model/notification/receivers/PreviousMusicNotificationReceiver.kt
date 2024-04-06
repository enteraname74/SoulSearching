package com.github.soulsearching.model.notification.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.soulsearching.model.playback.PlaybackManagerAndroidImpl
import com.github.soulsearching.model.playback.PlayerService

/**
 * Receiver for playing the previous song in the queue.
 */
class PreviousMusicNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val intentForNotification = Intent(PlaybackManagerAndroidImpl.BROADCAST_NOTIFICATION)
        intentForNotification.putExtra(PlaybackManagerAndroidImpl.PREVIOUS, true)
        context.sendBroadcast(intentForNotification)
    }
}