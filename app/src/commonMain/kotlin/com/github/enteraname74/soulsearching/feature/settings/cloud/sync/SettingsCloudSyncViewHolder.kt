package com.github.enteraname74.soulsearching.feature.settings.cloud.sync

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.soulsearching.domain.usecase.cloud.CloudBackgroundSyncJob
import com.github.enteraname74.soulsearching.domain.usecase.cloud.CommonCloudPreferencesUseCase
import com.github.enteraname74.soulsearching.domain.usecase.music.SyncDataWithCloudUseCase
import com.github.enteraname74.soulsearching.domain.utils.DateUiUtils
import com.github.enteraname74.soulsearching.viewholder.SoulViewModelHolderV2
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec

class SettingsCloudSyncViewHolder(
    private val cloudBackgroundSyncJob: CloudBackgroundSyncJob,
    private val commonCloudPreferencesUseCase: CommonCloudPreferencesUseCase,
) : SoulViewModelHolderV2<SettingsCloudSyncNavScope, SettingsCloudSyncState>() {

    init {
        viewModelScope.launch {
            cloudBackgroundSyncJob
                .state
                .collectLatest { state ->
                    updateState { copy(syncingState = state) }
                }
        }

        viewModelScope.launch {
            commonCloudPreferencesUseCase.observe().collectLatest { preferences ->
                updateState {
                    copy(
                        lastSync = preferences.lastSyncMillis?.let { DateUiUtils.formatToReadableDateTime(it).toTextSpec() }
                            ?: CoreRes.string.cloud_settings_no_sync.toTextSpec(),
                        lastStatisticsSync = preferences.lastStatisticsSyncMillis?.let { DateUiUtils.formatToReadableDateTime(it).toTextSpec() }
                            ?: CoreRes.string.cloud_settings_no_sync.toTextSpec(),
                        lastSettingsSync = preferences.lastSettingsSyncMillis?.let { DateUiUtils.formatToReadableDateTime(it).toTextSpec() }
                            ?: CoreRes.string.cloud_settings_no_sync.toTextSpec(),
                    )
                }
            }
        }
    }

    @Composable
    override fun Content(state: SettingsCloudSyncState) {
        SettingsCloudSyncScreen(
            state = state,
        )
    }

    override fun getInitialState(): SettingsCloudSyncState =
        SettingsCloudSyncState(
            lastSync = CoreRes.string.cloud_settings_no_sync.toTextSpec(),
            lastStatisticsSync = CoreRes.string.cloud_settings_no_sync.toTextSpec(),
            lastSettingsSync = CoreRes.string.cloud_settings_no_sync.toTextSpec(),
            syncingState = SyncDataWithCloudUseCase.State.Idle,
            navigateBack = { navigate { navigateBack() } },
            launchSync = ::launchSync,
        )

    private fun launchSync() {
        viewModelScope.launch {
            cloudBackgroundSyncJob.launchIfPossible(syncStats = true)
        }
    }
}
