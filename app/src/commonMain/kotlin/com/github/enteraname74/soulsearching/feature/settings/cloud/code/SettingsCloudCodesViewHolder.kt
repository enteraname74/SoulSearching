package com.github.enteraname74.soulsearching.feature.settings.cloud.code

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.user.UserInscriptionCode
import com.github.enteraname74.domain.repository.UserInscriptionCodeRepository
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.viewholder.SoulViewModelHolder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsCloudCodesViewHolder(
    private val userInscriptionCodeRepository: UserInscriptionCodeRepository,
    private val loadingManager: LoadingManager,
    private val feedbackPopUpManager: FeedbackPopUpManager,
): SoulViewModelHolder<
        SettingsCloudCodesActions,
        SettingsCloudCodesNavScope,
        SettingsCloudCodeState,
        >(
            initialState = SettingsCloudCodeState(
                codes = emptyList(),
            )
        ), SettingsCloudCodesActions {
    override val actions: SettingsCloudCodesActions = this

    override fun navigateBack() {
        navigate { navigateBack() }
    }

    init {
        viewModelScope.launch {
            userInscriptionCodeRepository
                .observeAll()
                .collectLatest { codes ->
                    updateState { copy(codes = codes) }
                }
        }

        loadingManager.withLoadingOnScope(viewModelScope) {
            feedbackPopUpManager.showErrorIfAny(userInscriptionCodeRepository.fetchAll())
        }
    }

    override fun generateCode() {
        loadingManager.withLoadingOnScope(viewModelScope) {
            feedbackPopUpManager.showErrorIfAny(userInscriptionCodeRepository.generateCode())
        }
    }

    override fun deleteCode(code: UserInscriptionCode) {
        loadingManager.withLoadingOnScope(viewModelScope) {
            feedbackPopUpManager.showErrorIfAny(userInscriptionCodeRepository.deleteCode(code))
        }
    }

    @Composable
    override fun Content(
        actions: SettingsCloudCodesActions,
        state: SettingsCloudCodeState
    ) {
        SettingsCloudCodesScreen(
            actions = actions,
            state = state,
        )
    }
}