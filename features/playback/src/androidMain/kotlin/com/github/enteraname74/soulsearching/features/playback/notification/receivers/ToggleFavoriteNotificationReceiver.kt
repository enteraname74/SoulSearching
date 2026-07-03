package com.github.enteraname74.soulsearching.features.playback.notification.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.enteraname74.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.features.playback.notification.impl.SoulSearchingAndroidNotification
import com.github.enteraname74.soulsearching.features.playback.notification.impl.SoulSearchingNotificationBelowAndroid13
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Receiver for changing the favorite state of the current played song.
 */
class ToggleFavoriteNotificationReceiver : BroadcastReceiver(), KoinComponent {
    private val workDispatcher: WorkDispatcher by inject()

    override fun onReceive(context: Context, intent: Intent?) {
        CoroutineScope(workDispatcher.dispatcher).launch {
            val intentForNotification = Intent(SoulSearchingAndroidNotification.BROADCAST_NOTIFICATION)
            intentForNotification.putExtra(SoulSearchingNotificationBelowAndroid13.FAVORITE, true)
            context.sendBroadcast(intentForNotification)
        }
    }
}
