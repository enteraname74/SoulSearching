package com.github.enteraname74.domain.usecase.cloud

import com.github.enteraname74.domain.usecase.music.SyncDataWithCloudUseCase
import kotlinx.coroutines.flow.StateFlow

// TODO SYNC: Add progress
interface CloudBackgroundSyncJob {
    val state: StateFlow<SyncDataWithCloudUseCase.State>

    suspend fun launchIfPossible()

    suspend fun cancelIfNeeded()
}
