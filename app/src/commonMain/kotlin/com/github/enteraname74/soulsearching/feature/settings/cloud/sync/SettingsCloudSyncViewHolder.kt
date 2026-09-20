package com.github.enteraname74.soulsearching.feature.settings.cloud.sync

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.usecase.cloud.CloudBackgroundSyncJob
import com.github.enteraname74.domain.usecase.cloud.CommonCloudPreferencesUseCase
import com.github.enteraname74.domain.usecase.music.SyncDataWithCloudUseCase
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.domain.utils.DateUiUtils
import com.github.enteraname74.soulsearching.viewholder.SoulViewModelHolderV2
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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
                        lastSync = preferences.lastSyncMillis?.let { DateUiUtils.formatToReadableDateTime(it) }
                            ?: strings.cloudSettingsNoSync,
                        lastStatisticsSync = preferences.lastStatisticsSyncMillis?.let { DateUiUtils.formatToReadableDateTime(it) }
                            ?: strings.cloudSettingsNoSync
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
            lastSync = strings.cloudSettingsNoSync,
            lastStatisticsSync = strings.cloudSettingsNoSync,
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
