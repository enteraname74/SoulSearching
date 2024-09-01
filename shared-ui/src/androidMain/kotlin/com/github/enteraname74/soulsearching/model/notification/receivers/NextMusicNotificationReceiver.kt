package com.github.enteraname74.soulsearching.model.notification.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.enteraname74.soulsearching.model.playback.PlaybackManagerAndroidImpl

/**
 * Receiver for playing the next song in the queue.
 */
class NextMusicNotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val intentForNotification = Intent(PlaybackManagerAndroidImpl.BROADCAST_NOTIFICATION)
        intentForNotification.putExtra(PlaybackManagerAndroidImpl.NEXT, true)
        context.sendBroadcast(intentForNotification)
    }
}