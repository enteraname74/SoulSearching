package com.github.soulsearching.service.notification.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.soulsearching.classes.PlayerUtils
import com.github.soulsearching.service.PlayerService

class NextMusicNotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        PlayerService.playNext()
    }
}