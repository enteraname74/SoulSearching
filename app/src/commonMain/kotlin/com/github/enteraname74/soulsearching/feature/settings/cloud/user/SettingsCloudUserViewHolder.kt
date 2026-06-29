package com.github.enteraname74.soulsearching.feature.settings.cloud.user

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.usecase.user.CommonUserUseCase
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.viewholder.SoulViewModelHolder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsCloudUserViewHolder(
    private val commonUserUseCase: CommonUserUseCase,
    private val loadingManager: LoadingManager,
) :
    SoulViewModelHolder<
            SettingsCloudUserActions,
            SettingsCloudUserNavScope,
            SettingsCloudUserState,
            >(
        initialState = SettingsCloudUserState(
            user = null,
        )
    ), SettingsCloudUserActions {
    override val actions: SettingsCloudUserActions = this

    init {
        viewModelScope.launch {
            commonUserUseCase
                .observeUser()
                .collectLatest { user ->
                    updateState { copy(user = user) }
                }
        }
    }

    override fun disconnect() {
        viewModelScope.launch {
            loadingManager.withLoading {
                commonUserUseCase.logout()
                navigateBack()
            }
        }
    }

    override fun navigateBack() {
        navigate { navigateBack() }
    }

    override fun toCode() {
        navigate { toCode() }
    }

    @Composable
    override fun Content(
        actions: SettingsCloudUserActions,
        state: SettingsCloudUserState
    ) {
        SettingsCloudUserScreen(
            actions = actions,
            state = state,
        )
    }
}