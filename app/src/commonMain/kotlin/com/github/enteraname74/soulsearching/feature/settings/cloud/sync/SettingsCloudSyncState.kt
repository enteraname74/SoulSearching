package com.github.enteraname74.soulsearching.feature.settings.cloud.sync

import com.github.enteraname74.domain.usecase.music.SyncDataWithCloudUseCase

data class SettingsCloudSyncState(
    val syncingState: SyncDataWithCloudUseCase.State,
)
