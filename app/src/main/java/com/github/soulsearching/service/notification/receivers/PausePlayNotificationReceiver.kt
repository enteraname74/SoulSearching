package com.github.soulsearching.service.notification.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.github.soulsearching.classes.PlayerUtils
import com.github.soulsearching.service.PlayerService

class PausePlayNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("PAUSE PLAY INTENT", "RECEIVE")
        PlayerUtils.playerViewModel.setPlayingState(context)
    }
}