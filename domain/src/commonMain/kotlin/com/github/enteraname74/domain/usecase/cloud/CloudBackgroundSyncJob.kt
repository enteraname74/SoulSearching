package com.github.enteraname74.domain.usecase.cloud

import com.github.enteraname74.domain.usecase.music.SyncDataWithCloudUseCase
import kotlinx.coroutines.flow.StateFlow

interface CloudBackgroundSyncJob {
    val state: StateFlow<SyncDataWithCloudUseCase.State>

    suspend fun launchIfPossible(
        syncStats: Boolean,
    )

    suspend fun cancelIfNeeded()
}
