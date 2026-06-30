package com.github.enteraname74.domain.usecase.cloud

import com.github.enteraname74.domain.usecase.music.SyncMusicWithCloudUseCase
import kotlinx.coroutines.flow.StateFlow

// TODO SYNC: Add progress
interface CloudBackgroundSyncJob {
    val state: StateFlow<SyncMusicWithCloudUseCase.State>

    suspend fun launchIfPossible()
}