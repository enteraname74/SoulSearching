package com.github.soulsearching

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.github.enteraname74.domain.domainModule
import com.github.enteraname74.localandroid.roomModule
import com.github.soulsearching.classes.notification.SoulSearchingNotification
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startKoin {
            androidContext(applicationContext)
            modules(viewModelModule, roomModule, domainModule)
        }
    }

    /**
     * Create the channel used by the Notification.
     */
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            SoulSearchingNotification.MUSIC_NOTIFICATION_CHANNEL_ID,
            getString(R.string.notification_name),
            NotificationManager.IMPORTANCE_LOW
        )
        channel.description = getString(R.string.notification_channel_description)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}