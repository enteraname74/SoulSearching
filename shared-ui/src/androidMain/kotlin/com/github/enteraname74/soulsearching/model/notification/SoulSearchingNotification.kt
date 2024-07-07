package com.github.soulsearching.model.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.MainActivity
import com.github.soulsearching.R
import com.github.soulsearching.model.notification.receivers.DeletedNotificationIntentReceiver

/**
 * Notification used for the playback by the service.
 */
abstract class SoulSearchingNotification(
    protected val context: Context,
    protected val mediaSessionToken: MediaSessionCompat.Token
) {
    protected val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    protected val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
        context,
        MUSIC_NOTIFICATION_CHANNEL_ID
    )
    protected lateinit var _notification: Notification
    val notification: Notification
        get() = _notification


    private val activityPendingIntent: PendingIntent = PendingIntent.getActivity(
        context,
        1,
        Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
        },
        PendingIntent.FLAG_IMMUTABLE
    )

    private val deleteNotificationIntent: PendingIntent = PendingIntent.getBroadcast(
        context,
        5,
        Intent(context, DeletedNotificationIntentReceiver::class.java),
        PendingIntent.FLAG_IMMUTABLE
    )

    /**
     * Initialize the notification.
     * It will show the information of the current played song if there is one.
     */
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    fun init(currentMusic: Music?) {
        notificationBuilder
            .setSmallIcon(R.drawable.ic_saxophone_svg)
            .setContentTitle(currentMusic?.name ?: "")
            .setContentText(currentMusic?.artist ?: "")
            .setContentIntent(activityPendingIntent)
            .setDeleteIntent(deleteNotificationIntent)
            .setSilent(true)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSessionToken)
            ).apply {
                priority = NotificationCompat.PRIORITY_LOW
            }

        _notification = notificationBuilder.build()
    }

    /**
     * Abstract method for updating the notification.
     */
    abstract fun update(isPlaying: Boolean)

    /**
     * Dismiss the notification.
     * It will cancel the notification and unregister the broadcast receiver linked to it.
     */
    fun release() {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(CHANNEL_ID)
    }

    companion object {
        const val MUSIC_NOTIFICATION_CHANNEL_ID = "SoulSearchingMusicNotificationChannel"
        const val CHANNEL_ID = 69
    }
}