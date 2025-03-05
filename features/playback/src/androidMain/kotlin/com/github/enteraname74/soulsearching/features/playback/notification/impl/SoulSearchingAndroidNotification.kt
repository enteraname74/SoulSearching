package com.github.enteraname74.soulsearching.features.playback.notification.impl

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.ui.graphics.ImageBitmap
import androidx.core.app.NotificationCompat
import com.github.enteraname74.soulsearching.features.playback.PlayerService
import com.github.enteraname74.soulsearching.features.playback.R
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManagerState
import com.github.enteraname74.soulsearching.features.playback.mediasession.MediaSessionManager
import com.github.enteraname74.soulsearching.features.playback.notification.SoulSearchingNotification
import com.github.enteraname74.soulsearching.features.playback.notification.receivers.DeletedNotificationIntentReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class SoulSearchingAndroidNotification(
    private val context: Context,
) : SoulSearchingNotification, KoinComponent {
    private val mediaSessionManager: MediaSessionManager by inject()

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    protected val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
        context,
        MUSIC_NOTIFICATION_CHANNEL_ID
    )
    lateinit var notification: Notification
        protected set

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

    private val deleteNotificationIntent: PendingIntent = PendingIntent.getBroadcast(
        context,
        5,
        Intent(context, DeletedNotificationIntentReceiver::class.java),
        PendingIntent.FLAG_IMMUTABLE
    )

    private var hasServiceBeenLaunched: Boolean = false

    protected fun NotificationCompat.Builder.soulNotificationBuilder(
        state: SoulSearchingAndroidNotificationState.Active
    ): NotificationCompat.Builder =
        this
            .setSmallIcon(R.drawable.app_logo)
            .setContentTitle(state.music.name)
            .setContentText(state.music.artist)
            .setContentIntent(activityPendingIntent)
            .setDeleteIntent(deleteNotificationIntent)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(state.mediaSessionToken)
            )

    abstract fun provideNotification(state: SoulSearchingAndroidNotificationState.Active): Notification

    private fun release() {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(CHANNEL_ID)
    }

    override suspend fun updateNotification(
        playbackManagerState: PlaybackManagerState.Data,
        cover: ImageBitmap?,
    ) {
        withContext(Dispatchers.Main) {
            val mediaSessionToken = mediaSessionManager.getUpdatedMediaSessionToken(
                playbackState = playbackManagerState,
            )

            notification = provideNotification(
                state = SoulSearchingAndroidNotificationState.Active(
                    music = playbackManagerState.currentMusic,
                    cover = cover,
                    isPlaying = playbackManagerState.isPlaying,
                    mediaSessionToken = mediaSessionToken
                )
            )
            if (!hasServiceBeenLaunched) {
                PlayerService.launchService(context = context)
                hasServiceBeenLaunched = true
            }
            notificationManager.notify(CHANNEL_ID, notification)

        }
    }

    override fun dismissNotification() {
        PlayerService.stopService(context = context)
        release()
        hasServiceBeenLaunched = false
    }

    companion object {
        fun buildNotification(
            context: Context,
            playbackManager: PlaybackManager,
        ): SoulSearchingNotification =
            if (Build.VERSION.SDK_INT >= 33) {
                SoulSearchingNotificationAndroid13(
                    context = context,
                )
            } else {
                SoulSearchingNotificationBelowAndroid13(
                    context = context,
                    playbackManager = playbackManager,
                )
            }

        const val MUSIC_NOTIFICATION_CHANNEL_ID = "SoulSearchingMusicNotificationChannel"
        const val CHANNEL_ID = 69

        const val BROADCAST_NOTIFICATION = "BROADCAST_NOTIFICATION"
        const val STOP_RECEIVE = "STOP RECEIVE"
        const val NEXT = "NEXT"
        const val PREVIOUS = "PREVIOUS"
        const val TOGGLE_PLAY_PAUSE = "TOGGLE PLAY PAUSE"
    }
}