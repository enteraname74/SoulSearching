package com.github.enteraname74.soulsearching

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.github.enteraname74.soulsearching.di.appModule
import com.github.enteraname74.soulsearching.feature.settings.cloud.worker.CloudSyncWorker
import com.github.enteraname74.soulsearching.features.playback.notification.impl.SoulSearchingAndroidNotification
import com.github.enteraname74.soulsearching.model.utils.StringsUtils
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        createNotificationChannel()
        startKoin {
            androidContext(applicationContext)
            workManagerFactory()
            modules(appModule)
        }
        super.onCreate()
    }

    /**
     * Create the channel used by the Notification.
     */
    private fun createNotificationChannel() {
        val strings = StringsUtils.getStrings(applicationContext)

        val musicNotificationChannel = NotificationChannel(
            SoulSearchingAndroidNotification.MUSIC_NOTIFICATION_CHANNEL_ID,
            strings.musicChannelNotificationName,
            NotificationManager.IMPORTANCE_LOW
        )
        val syncChannel = NotificationChannel(
            CloudSyncWorker.CHANNEL_ID,
            strings.cloudSyncChannelNotificationName,
            NotificationManager.IMPORTANCE_LOW
        )
        musicNotificationChannel.description = strings.musicChannelNotificationDescription
        syncChannel.description = strings.cloudSyncChannelNotificationDescription

        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(musicNotificationChannel)
        notificationManager.createNotificationChannel(syncChannel)
    }
}
