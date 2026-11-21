package com.github.enteraname74.soulsearching.feature.settings.cloud.user.data

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.soulsearching.domain.repository.UserRepository
import com.github.enteraname74.soulsearching.domain.usecase.user.ClearUserDataUseCase
import com.github.enteraname74.soulsearching.coreui.dialog.SoulAlertDialog
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.viewholder.SoulViewModelHolderV2
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec

class SettingsCloudUserDataViewHolder(
    private val clearUserDataUseCase: ClearUserDataUseCase,
    private val loadingManager: LoadingManager,
    private val feedbackPopUpManager: FeedbackPopUpManager,
    private val userRepository: UserRepository,
) : SoulViewModelHolderV2<SettingsCloudUserDataNavScope, SettingsCloudUserDataState>() {
    override fun getInitialState(): SettingsCloudUserDataState =
        SettingsCloudUserDataState(
            userStorage = null,
            dialogState = null,
            onClearData = ::showClearDataDialog,
            onNavigateBack = { navigate { navigateBack() } },
        )

    init {
        fetchUserStorage()
        viewModelScope.launch {
            userRepository
                .observeUserStorage()
                .collectLatest { userStorage ->
                    updateState { copy(userStorage = userStorage) }
                }
        }
    }

    private fun fetchUserStorage() {
        viewModelScope.launch {
            val result = userRepository.fetchUserStorage()
            feedbackPopUpManager.showErrorIfAny(result)
        }
    }

    private fun showClearDataDialog() {
        updateState {
            copy(
                dialogState = object : SoulDialog {
                    @Composable
                    override fun Dialog() {
                        SoulAlertDialog(
                            title = CoreRes.string.clear_user_storage_button.toTextSpec(),
                            text = CoreRes.string.clear_user_storage_dialog_text.toTextSpec(),
                            confirmAction = ::clearData,
                            dismissText = CoreRes.string.cancel.toTextSpec(),
                            confirmText = CoreRes.string.delete.toTextSpec(),
                            dismissAction = { updateState { copy(dialogState = null) } }
                        )
                    }
                }
            )
        }
    }

    private fun clearData() {
        loadingManager.withLoadingOnScope(viewModelScope) {
            updateState { copy(dialogState = null) }
            val result = clearUserDataUseCase()
            feedbackPopUpManager.showErrorIfAny(result)
        }
    }

    @Composable
    override fun Content(state: SettingsCloudUserDataState) {
        SettingsCloudUserDataScreen(state)
    }
}