package com.github.enteraname74.soulsearching.feature.settings.cloud.sync

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.usecase.cloud.CloudBackgroundSyncJob
import com.github.enteraname74.domain.usecase.music.SyncMusicWithCloudUseCase
import com.github.enteraname74.soulsearching.viewholder.SoulViewModelHolder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsCloudSyncViewHolder(
    private val cloudBackgroundSyncJob: CloudBackgroundSyncJob,
) : SoulViewModelHolder<
    SettingsCloudSyncActions,
    SettingsCloudSyncNavScope,
    SettingsCloudSyncState>(
    initialState = SettingsCloudSyncState(
        syncingState = SyncMusicWithCloudUseCase.State.Idle,
    )
), SettingsCloudSyncActions {
    override val actions: SettingsCloudSyncActions = this

    init {
        viewModelScope.launch {
            cloudBackgroundSyncJob
                .state
                .collectLatest { state ->
                    updateState { copy(syncingState = state) }
                }
        }
    }

    override fun launchSync() {
        viewModelScope.launch {
            cloudBackgroundSyncJob.launchIfPossible()
        }
    }

    override fun navigateBack() {
        navigate { navigateBack() }
    }

    @Composable
    override fun Content(
        actions: SettingsCloudSyncActions,
        state: SettingsCloudSyncState
    ) {
        SettingsCloudSyncScreen(
            actions = actions,
            state = state,
        )
    }
}