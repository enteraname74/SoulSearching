package com.github.enteraname74.soulsearching.settings.cloud.worker

import com.github.enteraname74.domain.usecase.cloud.CloudBackgroundSyncJob
import com.github.enteraname74.domain.usecase.music.SyncMusicWithCloudUseCase
import kotlinx.coroutines.flow.StateFlow

// TODO SYNC: Improve for non Android platforms.
class CloudBackgroundSyncJobNonAndroidImpl(
    private val syncMusicWithCloudUseCase: SyncMusicWithCloudUseCase,
) : CloudBackgroundSyncJob {
    override val state: StateFlow<SyncMusicWithCloudUseCase.State> = syncMusicWithCloudUseCase.state

    override suspend fun launchIfPossible() {
        syncMusicWithCloudUseCase()
    }

    override suspend fun cancelIfNeeded() {
        // no-op
    }
}