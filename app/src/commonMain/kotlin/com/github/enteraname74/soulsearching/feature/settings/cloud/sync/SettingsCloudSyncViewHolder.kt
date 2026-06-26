package com.github.enteraname74.soulsearching.feature.settings.cloud.sync

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.usecase.cloud.CloudBackgroundSyncJob
import com.github.enteraname74.soulsearching.viewholder.SoulViewModelHolder
import kotlinx.coroutines.launch

class SettingsCloudSyncViewHolder(
    private val cloudBackgroundSyncJob: CloudBackgroundSyncJob,
) : SoulViewModelHolder<
        SettingsCloudSyncActions,
        SettingsCloudSyncNavScope,
        SettingsCloudSyncState>(
            initialState = SettingsCloudSyncState(
                isSyncing = false,
            )
        ), SettingsCloudSyncActions {
    override val actions: SettingsCloudSyncActions = this

    override fun launchSync() {
        viewModelScope.launch {
            updateState { copy(isSyncing = true) }
            cloudBackgroundSyncJob.launchIfPossible()
            updateState { copy(isSyncing = false) }
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