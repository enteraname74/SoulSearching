package com.github.enteraname74.soulsearching.settings.cloud.worker

import com.github.enteraname74.domain.usecase.cloud.CloudBackgroundSyncJob
import com.github.enteraname74.domain.usecase.cloud.HasValidCloudInformationUseCase
import com.github.enteraname74.domain.usecase.music.SyncDataWithCloudUseCase
import com.github.enteraname74.domain.util.WorkDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

// TODO SYNC: Improve for non Android platforms.
class CloudBackgroundSyncJobNonAndroidImpl(
    private val syncDataWithCloudUseCase: SyncDataWithCloudUseCase,
    private val workDispatcher: WorkDispatcher,
    private val hasValidCloudInformationUseCase: HasValidCloudInformationUseCase,
) : CloudBackgroundSyncJob {
    override val state: StateFlow<SyncDataWithCloudUseCase.State> = syncDataWithCloudUseCase.state
    private var job: Job? = null

    override suspend fun launchIfPossible() {
        val hasValidCloudInformation: Boolean? = hasValidCloudInformationUseCase().firstOrNull()
        println("CLUELESS -- has valid infos? $hasValidCloudInformation")

        // No-op if no valid cloud information
        if (hasValidCloudInformation != true) return

        if (job?.isActive != true) {
            job = CoroutineScope(workDispatcher.dispatcher).launch {
                syncDataWithCloudUseCase()
            }
        }
    }

    override suspend fun cancelIfNeeded() {
        job?.cancel()
    }
}