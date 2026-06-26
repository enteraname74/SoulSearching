package com.github.enteraname74.soulsearching.feature.settings.cloud.worker

import android.content.Context
import androidx.work.*
import com.github.enteraname74.domain.usecase.cloud.CloudBackgroundSyncJob
import com.github.enteraname74.domain.usecase.cloud.HasValidCloudInformationUseCase
import kotlinx.coroutines.flow.firstOrNull

class CloudBackgroundSyncJobAndroidImpl(
    private val context: Context,
    private val hasValidCloudInformationUseCase: HasValidCloudInformationUseCase,
): CloudBackgroundSyncJob {
    override suspend fun launchIfPossible() {
        val hasValidCloudInformation: Boolean? = hasValidCloudInformationUseCase().firstOrNull()

        // No-op if no valid cloud information
        if (hasValidCloudInformation != true) return

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
            ExistingWorkPolicy.REPLACE,
            workRequest,
        )
    }

    companion object {
        private const val SYNC_WORKER_NAME = "SYNC_WORKER_NAME"
    }
}