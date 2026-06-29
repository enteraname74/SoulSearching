package com.github.enteraname74.soulsearching.feature.settings.cloud.code

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.usecase.user.CommonUserUseCase
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.feature.settings.cloud.SettingsCloudNavScope
import com.github.enteraname74.soulsearching.viewholder.SoulViewModelHolder
import kotlinx.coroutines.launch

class SettingsCloudCodeViewHolder(
    private val commonUserUseCase: CommonUserUseCase,
    private val loadingManager: LoadingManager,
    private val feedbackPopUpManager: FeedbackPopUpManager,
): SoulViewModelHolder<
        SettingsCloudCodeActions,
        SettingsCloudCodeNavScope,
        SettingsCloudCodeState,
        >(
            initialState = SettingsCloudCodeState(
                code = null,
            )
        ), SettingsCloudCodeActions {
    override val actions: SettingsCloudCodeActions = this

    override fun navigateBack() {
        navigate { navigateBack() }
    }

    override fun generateCode() {
        viewModelScope.launch {
            loadingManager.withLoading {
                when (val result = commonUserUseCase.generateCode()) {
                    is SoulResult.Error -> feedbackPopUpManager.showErrorIfAny(result)
                    is SoulResult.Success -> updateState { copy(code = result.data) }
                }
            }
        }
    }

    @Composable
    override fun Content(
        actions: SettingsCloudCodeActions,
        state: SettingsCloudCodeState
    ) {
        SettingsCloudCodeScreen(
            actions = actions,
            state = state,
        )
    }
}