package com.github.soulsearching.model.notification.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.soulsearching.model.playback.PlaybackManagerAndroidImpl
import com.github.soulsearching.model.playback.PlayerService

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