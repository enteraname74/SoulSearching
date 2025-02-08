package com.github.enteraname74.soulsearching.feature.settings.cloud.worker

import android.content.Context
import androidx.work.*

class SettingsCloudUploadLauncherAndroidImpl(
    private val context: Context
): SettingsCloudUploadLauncher {
    override suspend fun launchWorker() {
        val workRequest = OneTimeWorkRequestBuilder<SettingsCloudUploadWorker>()
            .setConstraints(
                Constraints
                    .Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            UPLOAD_WORKER_NAME,
            ExistingWorkPolicy.KEEP,
            workRequest,
        )
    }

    companion object {
        private const val UPLOAD_WORKER_NAME = "CLOUD_UPLOAD_DATA"
    }
}