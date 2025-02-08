package com.github.enteraname74.soulsearching

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.github.enteraname74.soulsearching.coreui.strings.EnStrings
import com.github.enteraname74.soulsearching.coreui.strings.FrStrings
import com.github.enteraname74.soulsearching.coreui.strings.Strings
import com.github.enteraname74.soulsearching.di.appModule
import com.github.enteraname74.soulsearching.feature.settings.cloud.worker.SettingsCloudUploadWorker
import com.github.enteraname74.soulsearching.features.playback.notification.impl.SoulSearchingAndroidNotification
import com.github.enteraname74.soulsearching.model.utils.StringsUtils
import com.github.soulsearching.R
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
        val strings = StringsUtils.getStrings()

        val musicChannel = NotificationChannel(
            SoulSearchingAndroidNotification.MUSIC_NOTIFICATION_CHANNEL_ID,
            strings.musicNotificationTitle,
            NotificationManager.IMPORTANCE_LOW
        )
        val uploadChannel = NotificationChannel(
            SettingsCloudUploadWorker.CHANNEL_ID,
            strings.uploadNotificationTitle,
            NotificationManager.IMPORTANCE_LOW
        )

        musicChannel.description = strings.musicNotificationText
        uploadChannel.description = strings.uploadNotificationText

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(musicChannel)
        notificationManager.createNotificationChannel(uploadChannel)
    }
}