package com.github.soulsearching.service.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.media.session.MediaSessionCompat.Token
import android.util.Log
import androidx.core.app.NotificationCompat
import com.github.soulsearching.MainActivity
import com.github.soulsearching.R
import com.github.soulsearching.classes.PlayerUtils
import com.github.soulsearching.classes.player.SoulSearchingMediaPlayerImpl
import com.github.soulsearching.service.notification.receivers.ChangeFavoriteStateNotificationReceiver
import com.github.soulsearching.service.notification.receivers.DeletedNotificationIntentReceiver

class SoulSearchingNotificationService(
    private val context : Context,
    private val mediaSessionToken: Token
    ) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val notificationMusicPlayer : NotificationCompat.Builder = NotificationCompat.Builder(context, MUSIC_NOTIFICATION_CHANNEL_ID)
    private lateinit var deleteNotificationIntent : PendingIntent
    private lateinit var changeFavoriteStateIntent : PendingIntent

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("RECEIVE","RECEIVE")
            if (intent.extras?.getBoolean("STOP_RECEIVE") != null && intent.extras?.getBoolean("STOP_RECEIVE") as Boolean) {
                context.unregisterReceiver(this)
//                PlayerService.stopMusic(context)
            } else {
                updateNotification()
            }
        }
    }

    fun initializeNotification(){
//        createNotificationChannel()

        context.registerReceiver(broadcastReceiver, IntentFilter(SoulSearchingMediaPlayerImpl.BROADCAST_NOTIFICATION))

        val activityIntent = Intent(context, MainActivity::class.java)
        activityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        activityIntent.action = Intent.ACTION_MAIN
        activityIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        val activityPendingIntent = PendingIntent.getActivity(
            context,
            1,
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        deleteNotificationIntent = PendingIntent.getBroadcast(
            context,
            5,
            Intent(context, DeletedNotificationIntentReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        changeFavoriteStateIntent = PendingIntent.getBroadcast(
            context,
            6,
            Intent(context, ChangeFavoriteStateNotificationReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        notificationMusicPlayer
            .setSmallIcon(R.drawable.ic_saxophone_svg)
            .setContentTitle(if (PlayerUtils.playerViewModel.currentMusic != null) PlayerUtils.playerViewModel.currentMusic?.name else "")
            .setContentText(if (PlayerUtils.playerViewModel.currentMusic != null) PlayerUtils.playerViewModel.currentMusic?.artist else "")
            .setContentIntent(activityPendingIntent)
            .setDeleteIntent(deleteNotificationIntent)
    }

    fun updateNotification() {
        notificationMusicPlayer
            .clearActions()
//            .addAction(R.drawable.ic_pause, "favoriteState", changeFavoriteStateIntent)
//            .addAction(R.drawable.ic_baseline_favorite_border,"next",nextMusicIntent)
            .setDeleteIntent(deleteNotificationIntent)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0, 1, 2)
                .setMediaSession(mediaSessionToken)
            )
        notificationManager.notify(CHANNEL_ID, notificationMusicPlayer.build())
    }

    fun dismissNotification() {
        context.unregisterReceiver(broadcastReceiver)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(CHANNEL_ID)
    }

    companion object {
        const val MUSIC_NOTIFICATION_CHANNEL_ID = "SoulSearchingMusicNotificationChannel"
        const val CHANNEL_ID = 69
    }
}