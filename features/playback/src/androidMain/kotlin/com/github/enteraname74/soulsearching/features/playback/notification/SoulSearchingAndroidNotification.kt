package com.github.enteraname74.soulsearching.features.playback.notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper
import com.github.enteraname74.soulsearching.features.playback.PlayerService
import com.github.enteraname74.soulsearching.features.playback.R
import com.github.enteraname74.soulsearching.features.playback.mediasession.MediaSessionManager
import com.github.enteraname74.soulsearching.features.playback.model.UpdateData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SoulSearchingAndroidNotification(
    private val context: Context,
) : SoulSearchingNotification, KoinComponent {
    private val mediaSessionManager: MediaSessionManager by inject()

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
        context,
        MUSIC_NOTIFICATION_CHANNEL_ID
    )
    lateinit var notification: Notification
        private set

    private val activityPendingIntent: PendingIntent = PendingIntent.getActivity(
        context,
        0,
        Intent().apply {
            setClassName(context.packageName, "com.github.enteraname74.soulsearching.MainActivity")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
        },
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            PendingIntent.FLAG_MUTABLE
        else
            PendingIntent.FLAG_UPDATE_CURRENT
    )

    private var hasServiceBeenLaunched: Boolean = false

    @OptIn(UnstableApi::class)
    private fun NotificationCompat.Builder.soulNotificationBuilder(
        updateData: UpdateData,
        mediaSession: MediaSession,
    ): NotificationCompat.Builder =
        this
            .setSmallIcon(R.drawable.app_logo)
            .setContentTitle(updateData.music.name)
            .setContentText(updateData.music.artistsNames)
            .setContentIntent(activityPendingIntent)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setStyle(
                MediaStyleNotificationHelper.MediaStyle(mediaSession)
            )

    fun provideNotification(
        updateData: UpdateData,
        mediaSession: MediaSession,
    ): Notification =
        notificationBuilder
            .soulNotificationBuilder(
                updateData = updateData,
                mediaSession = mediaSession,
            )
            .build()

    private fun release() {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(CHANNEL_ID)
    }

    override suspend fun update(
        updateData: UpdateData,
    ) {
        withContext(Dispatchers.Main) {
            val mediaSession = mediaSessionManager.getUpdatedMediaSession(
                updateData = updateData,
            )

            notification = provideNotification(
                updateData = updateData,
                mediaSession = mediaSession,
            )
            if (!hasServiceBeenLaunched) {
                PlayerService.launchService(context = context)
                hasServiceBeenLaunched = true
            }
            notificationManager.notify(CHANNEL_ID, notification)

        }
    }

    override fun dismiss() {
        PlayerService.stopService(context = context)
        release()
        hasServiceBeenLaunched = false
    }

    companion object {
        const val MUSIC_NOTIFICATION_CHANNEL_ID: String = "SoulSearchingMusicNotificationChannel"
        const val CHANNEL_ID: Int = 69

        const val BROADCAST_NOTIFICATION: String = "BROADCAST_NOTIFICATION"

        const val STOP_RECEIVE: String = "STOP RECEIVE"
    }
}