package com.github.soulsearching.classes.notification.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.soulsearching.service.PlayerService

/**
 * Receiver for playing the next song in the queue.
 */
class NextMusicNotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        PlayerService.playNext()
    }
}