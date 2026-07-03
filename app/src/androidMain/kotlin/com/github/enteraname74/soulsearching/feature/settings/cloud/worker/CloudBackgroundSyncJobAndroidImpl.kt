package com.github.enteraname74.soulsearching.feature.settings.cloud.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.github.enteraname74.domain.usecase.cloud.CloudBackgroundSyncJob
import com.github.enteraname74.domain.usecase.cloud.HasValidCloudInformationUseCase
import com.github.enteraname74.domain.usecase.music.SyncMusicWithCloudUseCase
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull

class CloudBackgroundSyncJobAndroidImpl(
    private val context: Context,
    private val hasValidCloudInformationUseCase: HasValidCloudInformationUseCase,
    syncMusicWithCloudUseCase: SyncMusicWithCloudUseCase,
) : CloudBackgroundSyncJob {
    override val state: StateFlow<SyncMusicWithCloudUseCase.State> = syncMusicWithCloudUseCase.state

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
