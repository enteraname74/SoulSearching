package com.github.enteraname74.soulsearching.feature.settings.cloud.sync

import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.domain.usecase.music.SyncDataWithCloudUseCase

data class SettingsCloudSyncState(
    val syncingState: SyncDataWithCloudUseCase.State,
    val lastSync: SoulTextSpec,
    val lastStatisticsSync: SoulTextSpec,
    val lastSettingsSync: SoulTextSpec,
    val navigateBack: () -> Unit,
    val launchSync: () -> Unit,
)
