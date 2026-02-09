package com.github.enteraname74.soulsearching.features.playback.notification.impl

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.content.ContextCompat
import com.github.enteraname74.domain.usecase.music.ToggleMusicFavoriteStatusUseCase
import com.github.enteraname74.soulsearching.features.playback.R
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.features.playback.notification.receivers.NextMusicNotificationReceiver
import com.github.enteraname74.soulsearching.features.playback.notification.receivers.PausePlayNotificationReceiver
import com.github.enteraname74.soulsearching.features.playback.notification.receivers.PreviousMusicNotificationReceiver
import com.github.enteraname74.soulsearching.features.playback.notification.receivers.ToggleFavoriteNotificationReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Specification of a SoulSearchingNotification for devices below Android 13.
 */
class SoulSearchingNotificationBelowAndroid13(
    context: Context,
    playbackManager: PlaybackManager,
    private val toggleMusicFavoriteStatusUseCase: ToggleMusicFavoriteStatusUseCase,
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

    private val toggleFavoriteIntent: PendingIntent = PendingIntent.getBroadcast(
        context,
        6,
        Intent(context, ToggleFavoriteNotificationReceiver::class.java),
        PendingIntent.FLAG_IMMUTABLE
    )

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val broadcastContext = this
            val extras = intent.extras ?: return

            CoroutineScope(Dispatchers.IO).launch {
                when {
                    extras.getBoolean(STOP_RECEIVE) -> context.unregisterReceiver(broadcastContext)
                    extras.getBoolean(NEXT) -> playbackManager.next()
                    extras.getBoolean(PREVIOUS) -> playbackManager.previous()
                    extras.getBoolean(TOGGLE_PLAY_PAUSE) -> playbackManager.togglePlayPause()
                    extras.getBoolean(FAVORITE) -> {
                        playbackManager.currentSong.value?.musicId?.let {
                            toggleMusicFavoriteStatusUseCase(musicId = it)
                        }
                    }
                }
            }
        }
    }

    init {
        ContextCompat.registerReceiver(
            context,
            broadcastReceiver,
            IntentFilter(BROADCAST_NOTIFICATION),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    private fun buildNotification(
        state: SoulSearchingAndroidNotificationState.Active,
    ): Notification {
        val pausePlayIcon = if (state.isPlaying) {
            R.drawable.ic_pause
        } else {
            R.drawable.ic_play_arrow
        }

        val favoriteIcon = if (state.isInFavorite) {
            R.drawable.ic_favorite_filled
        } else {
            R.drawable.ic_favorite
        }

        return notificationBuilder
            .clearActions()
            .soulNotificationBuilder(state)
            .addAction(R.drawable.ic_skip_previous, "previous", previousMusicIntent)
            .addAction(pausePlayIcon, "pausePlay", pausePlayIntent)
            .addAction(R.drawable.ic_skip_next, "next", nextMusicIntent)
            .addAction(favoriteIcon, "favorite", toggleFavoriteIntent)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2, 3)
                    .setMediaSession(state.mediaSessionToken)
            )
            .setLargeIcon(state.cover?.asAndroidBitmap())
            .build()
    }

    override fun provideNotification(state: SoulSearchingAndroidNotificationState.Active): Notification =
        buildNotification(state = state)

    companion object {
        const val STOP_RECEIVE = "STOP RECEIVE"
        const val NEXT = "NEXT"
        const val PREVIOUS = "PREVIOUS"
        const val FAVORITE = "FAVORITE"
        const val TOGGLE_PLAY_PAUSE = "TOGGLE PLAY PAUSE"
    }
}