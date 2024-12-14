package com.github.enteraname74.soulsearching.features.playback.notification.impl

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.compose.ui.graphics.asAndroidBitmap
import com.github.enteraname74.soulsearching.features.playback.R
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.features.playback.notification.receivers.NextMusicNotificationReceiver
import com.github.enteraname74.soulsearching.features.playback.notification.receivers.PausePlayNotificationReceiver
import com.github.enteraname74.soulsearching.features.playback.notification.receivers.PreviousMusicNotificationReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Specification of a SoulSearchingNotification for devices below Android 13.
 */
class SoulSearchingNotificationBelowAndroid13(
    context: Context,
    playbackManager: PlaybackManager,
) : SoulSearchingAndroidNotification(
    context = context,
) {
    private val previousMusicIntent: PendingIntent = PendingIntent.getBroadcast(
        context,
        2,
        Intent(context, PreviousMusicNotificationReceiver::class.java),
        PendingIntent.FLAG_IMMUTABLE
    )

    private val pausePlayIntent: PendingIntent = PendingIntent.getBroadcast(
        context,
        3,
        Intent(context, PausePlayNotificationReceiver::class.java),
        PendingIntent.FLAG_IMMUTABLE
    )

    private val nextMusicIntent: PendingIntent = PendingIntent.getBroadcast(
        context,
        4,
        Intent(context, NextMusicNotificationReceiver::class.java),
        PendingIntent.FLAG_IMMUTABLE
    )

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val broadcastContext = this

            CoroutineScope(Dispatchers.IO).launch {
                if (intent.extras?.getBoolean(STOP_RECEIVE) == true) {
                    context.unregisterReceiver(broadcastContext)
                } else if (intent.extras?.getBoolean(NEXT) == true) {
                    playbackManager.next()
                } else if (intent.extras?.getBoolean(PREVIOUS) == true) {
                    playbackManager.previous()
                } else if(intent.extras?.getBoolean(TOGGLE_PLAY_PAUSE) == true) {
                    playbackManager.togglePlayPause()
                }
            }
        }
    }

    init {
        if (Build.VERSION.SDK_INT >= 33) {
            context.registerReceiver(
                broadcastReceiver,
                IntentFilter(BROADCAST_NOTIFICATION),
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            context.registerReceiver(
                broadcastReceiver,
                IntentFilter(BROADCAST_NOTIFICATION)
            )
        }
    }

    private fun buildNotification(
        state: SoulSearchingAndroidNotificationState.Active,
    ): Notification {
        val pausePlayIcon = if (state.isPlaying) {
            R.drawable.ic_pause
        } else {
            R.drawable.ic_play_arrow
        }

        return notificationBuilder
            .clearActions()
            .soulNotificationBuilder(state)
            .addAction(R.drawable.ic_skip_previous,"previous",previousMusicIntent)
            .addAction(pausePlayIcon,"pausePlay",pausePlayIntent)
            .addAction(R.drawable.ic_skip_next,"next",nextMusicIntent)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0,1,2)
                    .setMediaSession(state.mediaSessionToken)
            )
            .setLargeIcon(state.cover?.asAndroidBitmap())
            .build()
    }

    override fun provideNotification(state: SoulSearchingAndroidNotificationState.Active): Notification =
        buildNotification(state = state)
}