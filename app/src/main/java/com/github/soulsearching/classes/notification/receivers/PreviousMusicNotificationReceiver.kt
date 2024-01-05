package com.github.soulsearching.classes.notification.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.soulsearching.service.PlayerService

/**
 * Receiver for playing the previous song in the queue.
 */
class PreviousMusicNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        PlayerService.playPrevious()
    }
}