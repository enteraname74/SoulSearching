package com.github.enteraname74.soulsearching.model.notification.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.enteraname74.soulsearching.model.playback.PlaybackManagerAndroidImpl

/**
 * Receiver for playing or pausing the current played song.
 */
class PausePlayNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val intentForNotification = Intent(PlaybackManagerAndroidImpl.BROADCAST_NOTIFICATION)
        intentForNotification.putExtra(PlaybackManagerAndroidImpl.TOGGLE_PLAY_PAUSE, true)
        context.sendBroadcast(intentForNotification)
    }
}