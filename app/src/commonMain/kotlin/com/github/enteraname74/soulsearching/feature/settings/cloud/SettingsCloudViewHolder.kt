package com.github.enteraname74.soulsearching.feature.settings.cloud

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.usecase.cloud.CommonCloudPreferencesUseCase
import com.github.enteraname74.soulsearching.viewholder.SoulViewModelHolder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsCloudViewHolder(
    private val commonCloudPreferencesUseCase: CommonCloudPreferencesUseCase,
) :
    SoulViewModelHolder<SettingsCloudActions, SettingsCloudNavScope, SettingsCloudState>(
        initialState = SettingsCloudState(
            user = null,
            hasUrl = false,
        )
    ),
    SettingsCloudActions {

    init {
        viewModelScope.launch {
            commonCloudPreferencesUseCase
                .observeUrl()
                .collectLatest {
                    updateState { copy(hasUrl = it != null) }
                }
        }
    }

    override val actions: SettingsCloudActions = this

    override fun toConnection() {
        navigate { toConnection() }
    }

    override fun toSettings() {
        navigate { toSettings() }
    }

    override fun navigateBack() {
        navigate { navigateBack() }
    }

    override val content: @Composable ((SettingsCloudActions, SettingsCloudState) -> Unit) =
        { actions, state ->
            SettingsCloudScreen(
                actions = actions,
                state = state,
            )
        }
}