package com.github.enteraname74.soulsearching.feature.settings.cloud.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

actual class CloudBackgroundSyncJob(
    private val context: Context,
) {
    actual fun launch() {
        val workRequest = OneTimeWorkRequestBuilder<CloudSyncWorker>()
            .setConstraints(
                Constraints
                    .Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            SYNC_WORKER_NAME,
            ExistingWorkPolicy.KEEP,
            workRequest,
        ).state
    }

    companion object {
        private const val SYNC_WORKER_NAME = "SYNC_WORKER_NAME"
    }
}