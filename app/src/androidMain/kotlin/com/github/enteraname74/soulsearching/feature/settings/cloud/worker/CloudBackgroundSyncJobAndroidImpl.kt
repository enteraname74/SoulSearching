package com.github.enteraname74.soulsearching.feature.settings.cloud.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.github.enteraname74.soulsearching.domain.usecase.cloud.CloudBackgroundSyncJob
import com.github.enteraname74.soulsearching.domain.usecase.cloud.HasValidCloudInformationUseCase
import com.github.enteraname74.soulsearching.domain.usecase.music.SyncDataWithCloudUseCase
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull

class CloudBackgroundSyncJobAndroidImpl(
    private val context: Context,
    private val hasValidCloudInformationUseCase: HasValidCloudInformationUseCase,
    syncDataWithCloudUseCase: SyncDataWithCloudUseCase,
) : CloudBackgroundSyncJob {
    override val state: StateFlow<SyncDataWithCloudUseCase.State> = syncDataWithCloudUseCase.state

    override suspend fun launchIfPossible(
        syncStats: Boolean,
    ) {
        val hasValidCloudInformation: Boolean? = hasValidCloudInformationUseCase().firstOrNull()

        // No-op if no valid cloud information
        if (hasValidCloudInformation != true) return

        val workRequest = OneTimeWorkRequestBuilder<CloudSyncWorker>()
            .setInputData(
                workDataOf(CloudSyncWorker.SYNC_STATS_INPUT_KEY to syncStats)
            )
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
        )
    }

    override suspend fun cancelIfNeeded() {
        WorkManager.getInstance(context).cancelUniqueWork(SYNC_WORKER_NAME)
    }

    companion object {
        private const val SYNC_WORKER_NAME = "SYNC_WORKER_NAME"
    }
}
