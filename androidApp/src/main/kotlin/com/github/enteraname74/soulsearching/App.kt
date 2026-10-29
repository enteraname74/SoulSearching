package com.github.enteraname74.soulsearching

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.github.enteraname74.soulsearching.di.appModule
import com.github.enteraname74.soulsearching.feature.settings.cloud.worker.CloudSyncWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.cloud_sync_channel_notification_name
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.cloud_sync_channel_notification_description
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString

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
        val syncChannel = runBlocking {
            NotificationChannel(
                CloudSyncWorker.CHANNEL_ID,
                getString(CoreRes.string.cloud_sync_channel_notification_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(CoreRes.string.cloud_sync_channel_notification_description)
            }
        }

        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(syncChannel)
    }
}
