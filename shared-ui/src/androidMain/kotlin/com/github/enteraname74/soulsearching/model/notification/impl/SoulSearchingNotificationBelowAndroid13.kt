package com.github.enteraname74.soulsearching.model.notification.impl

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.media.session.MediaSessionCompat
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.app.NotificationCompat
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.soulsearching.R
import com.github.enteraname74.soulsearching.model.notification.SoulSearchingNotification
import com.github.enteraname74.soulsearching.model.notification.receivers.NextMusicNotificationReceiver
import com.github.enteraname74.soulsearching.model.notification.receivers.PausePlayNotificationReceiver
import com.github.enteraname74.soulsearching.model.notification.receivers.PreviousMusicNotificationReceiver
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Specification of a SoulSearchingNotification for devices below Android 13.
 */
class SoulSearchingNotificationBelowAndroid13(
    context: Context,
    mediaSessionToken: MediaSessionCompat.Token
) : SoulSearchingNotification(
    context,
    mediaSessionToken
), KoinComponent {
    private val playbackManager: PlaybackManager by inject()

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

    private fun buildNotification(
        isPlaying: Boolean,
    ): Notification {
        val pausePlayIcon = if (isPlaying) {
            R.drawable.ic_pause
        } else {
            R.drawable.ic_play_arrow
        }

        return notificationBuilder
            .clearActions()
            .setContentIntent(activityPendingIntent)
            .setDeleteIntent(deleteNotificationIntent)
            .setSmallIcon(R.drawable.ic_saxophone_svg)
            .setSilent(true)
            .addAction(R.drawable.ic_skip_previous,"previous",previousMusicIntent)
            .addAction(pausePlayIcon,"pausePlay",pausePlayIntent)
            .addAction(R.drawable.ic_skip_next,"next",nextMusicIntent)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0,1,2)
                    .setMediaSession(mediaSessionToken)
            )
            .setLargeIcon(playbackManager.currentMusicCover?.asAndroidBitmap())
            .setContentTitle(playbackManager.currentMusic?.name.orEmpty())
            .setContentText(playbackManager.currentMusic?.artist.orEmpty())
            .apply {
                priority = NotificationCompat.PRIORITY_LOW
            }
            .build()
    }

    override fun init(currentMusic: Music?) {
        _notification = buildNotification(isPlaying = playbackManager.isPlaying)
    }

    override fun update(isPlaying: Boolean) {
        _notification = buildNotification(isPlaying = isPlaying)
        notificationManager.notify(CHANNEL_ID, _notification)
    }
}