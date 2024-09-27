package com.github.enteraname74.soulsearching.model.playback

import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.ServiceCompat
import com.github.enteraname74.soulsearching.model.notification.SoulSearchingNotification
import com.github.enteraname74.soulsearching.model.notification.SoulSearchingNotificationBuilder

/**
 * Service used for the playback.
 */
class PlayerService : Service() {
    private var musicNotification: SoulSearchingNotification? = null

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.extras?.getBoolean(UPDATE_WITH_PLAYING_STATE) != null) {
                val isPlaying = intent.extras!!.getBoolean(UPDATE_WITH_PLAYING_STATE)
                musicNotification?.update(isPlaying)
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Suppress("Deprecation")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("Player service", "ON START COMMAND")
        val token: MediaSessionCompat.Token? = intent?.extras?.get(MEDIA_SESSION_TOKEN) as MediaSessionCompat.Token?

        if (Build.VERSION.SDK_INT >= 33) {
            applicationContext.registerReceiver(
                broadcastReceiver,
                IntentFilter(SERVICE_BROADCAST),
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            applicationContext.registerReceiver(
                broadcastReceiver,
                IntentFilter(SERVICE_BROADCAST)
            )
        }

        token?.let {
            musicNotification = SoulSearchingNotificationBuilder.buildNotification(
                context = this,
                mediaSessionToken = token
            )
            musicNotification!!.init(null)
            ServiceCompat.startForeground(
                this,
                SoulSearchingNotification.CHANNEL_ID,
                musicNotification!!.notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK,
            )
        }

        return START_STICKY
    }

    /**
     * Stop the service and remove its notification.
     */
    private fun stopService() {
        musicNotification?.release()
        musicNotification = null

        val serviceIntent = Intent(this, PlayerService::class.java)
        stopService(serviceIntent)
        try {
            this.unregisterReceiver(broadcastReceiver)
        } catch (_: Exception) {
            Log.e("Player Service", "Tried to unregister the broadcast receiver, but couldn't.")
        }
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
        const val MEDIA_SESSION_TOKEN = "TOKEN"
        const val UPDATE_WITH_PLAYING_STATE = "UPDATE"
    }
}