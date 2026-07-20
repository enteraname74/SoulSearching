package com.github.enteraname74.soulsearching.feature.settings.cloud

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.usecase.cloud.CommonCloudPreferencesUseCase
import com.github.enteraname74.domain.usecase.user.CommonUserUseCase
import com.github.enteraname74.soulsearching.viewholder.SoulViewModelHolder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsCloudViewHolder(
    private val commonCloudPreferencesUseCase: CommonCloudPreferencesUseCase,
    private val commonUserUseCase: CommonUserUseCase,
) :
    SoulViewModelHolder<SettingsCloudActions, SettingsCloudNavScope, SettingsCloudState>(
        initialState = SettingsCloudState(
            user = null,
            hasUrl = null,
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

        viewModelScope.launch {
            commonUserUseCase
                .observeUser()
                .collectLatest {
                    updateState { copy(user = it) }
                }
        }
    }

    override val actions: SettingsCloudActions = this

    override fun onUserClick() {
        if (currentState.user == null) {
            navigate { toConnection() }
        } else {
            navigate { toUser() }
        }
    }

    override fun toSettings() {
        navigate { toSettings() }
    }

    override fun navigateBack() {
        navigate { navigateBack() }
    }

    override fun toSync() {
        navigate { toSync() }
    }

    override fun toSharedList() {
        navigate { toSharedList() }
    }

    override fun toFetchMusic() {
        navigate { toFetchMusic() }
    }

    override fun toUsers() {
        navigate { toUsers() }
    }

    override fun toExplanations() {
        navigate { toExplanations() }
    }

    @Composable
    override fun Content(
        actions: SettingsCloudActions,
        state: SettingsCloudState
    ) {
        SettingsCloudScreen(
            actions = actions,
            state = state,
        )
    }
}