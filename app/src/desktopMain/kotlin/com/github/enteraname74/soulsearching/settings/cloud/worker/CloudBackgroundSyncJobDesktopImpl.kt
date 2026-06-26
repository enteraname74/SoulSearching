package com.github.enteraname74.soulsearching.settings.cloud.worker

import com.github.enteraname74.domain.usecase.cloud.CloudBackgroundSyncJob
import com.github.enteraname74.domain.usecase.music.SyncMusicWithCloudUseCase

// TODO SYNC: Improve for Desktop.
class CloudBackgroundSyncJobDesktopImpl(
    private val syncMusicWithCloudUseCase: SyncMusicWithCloudUseCase,
) : CloudBackgroundSyncJob {
    override suspend fun launchIfPossible() {
        syncMusicWithCloudUseCase()
    }
}