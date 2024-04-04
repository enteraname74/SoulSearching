package com.github.soulsearching.model.playback

import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import com.github.soulsearching.model.notification.SoulSearchingNotification
import com.github.soulsearching.model.notification.SoulSearchingNotificationBuilder
import com.github.soulsearching.model.player.SoulSearchingAndroidPlayerImpl
import com.github.soulsearching.model.settings.SoulSearchingSettings
import com.github.soulsearching.utils.PlayerUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 * Service used for the playback.
 */
class PlayerService : Service() {
    private var notification: SoulSearchingNotification? = null

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.extras?.getBoolean(UPDATE_WITH_PLAYING_STATE) != null) {
                val isPlaying = intent.extras!!.getBoolean(UPDATE_WITH_PLAYING_STATE)
                notification?.update(isPlaying)
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

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
            notification = SoulSearchingNotificationBuilder.buildNotification(
                context = this,
                mediaSessionToken = token
            )
            notification!!.init(null)
            startForeground(SoulSearchingNotification.CHANNEL_ID, notification!!.getPlayerNotification())
        }

        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        notification?.dismissNotification()

        val serviceIntent = Intent(this, PlayerService::class.java)
        this.unregisterReceiver(broadcastReceiver)
        stopService(serviceIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    companion object {
        const val RESTART_SERVICE = "RESTART_SERVICE"
        const val SERVICE_BROADCAST = "SERVICE_BROADCAST"
        const val MEDIA_SESSION_TOKEN = "TOKEN"
        const val UPDATE_WITH_PLAYING_STATE = "UPDATE"
    }
}