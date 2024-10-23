package com.github.enteraname74.soulsearching.features.playback

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import android.util.Log
import androidx.core.app.ServiceCompat
import com.github.enteraname74.soulsearching.features.playback.notification.impl.SoulSearchingAndroidNotification
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Service used for the playback.
 */
class PlayerService : Service(), KoinComponent {
    private val musicNotification: SoulSearchingAndroidNotification by inject()

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Suppress("Deprecation")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("Player service", "ON START COMMAND")
        ServiceCompat.startForeground(
            this,
            SoulSearchingAndroidNotification.CHANNEL_ID,
            musicNotification.notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK,
        )

        return START_STICKY
    }

    /**
     * Stop the service and remove its notification.
     */
    private fun stopService() {
        val serviceIntent = Intent(this, PlayerService::class.java)
        stopService(serviceIntent)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.i("Player service", "ON TASK REMOVED")
        try {
            stopService()
        } catch (_: Exception) {
            Log.e("Player Service", "Failed to stop service in onTaskRemoved")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("Player service", "ON DESTROY")
        try {
            stopService()
            stopForeground(STOP_FOREGROUND_REMOVE)
        } catch (_: Exception) {
            Log.e("Player Service", "Failed to stop service in onDestroy")
        }
    }

    companion object {
        const val RESTART_SERVICE = "RESTART_SERVICE"
        const val SERVICE_BROADCAST = "SERVICE_BROADCAST"

        /**
         * Launch the foreground service used to handle the notification.
         * It gives the token of the media session manager to start the notification.
         */
        fun launchService(context: Context) {
            val serviceIntent = Intent(context, PlayerService::class.java)
            context.startForegroundService(serviceIntent)
        }

        /**
         * Stop the service used to emit the music notification.
         */
        fun stopService(context: Context) {
            val serviceIntent = Intent(context, PlayerService::class.java)
            context.stopService(serviceIntent)
        }
    }
}