package com.github.enteraname74.soulsearching.feature.settings.cloud.sync

import com.github.enteraname74.domain.usecase.music.SyncMusicWithCloudUseCase

data class SettingsCloudSyncState(
    val syncingState: SyncMusicWithCloudUseCase.State,
)
