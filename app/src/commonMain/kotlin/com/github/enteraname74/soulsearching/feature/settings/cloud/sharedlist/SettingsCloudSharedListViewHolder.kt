package com.github.enteraname74.soulsearching.feature.settings.cloud.sharedlist

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.usecase.player.JoinSharedPlayedListUseCase
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolderImpl
import com.github.enteraname74.soulsearching.viewholder.SoulViewModelHolder

class SettingsCloudSharedListViewHolder(
    private val savedStateHandle: SavedStateHandle,
    private val joinSharedPlayedListUseCase: JoinSharedPlayedListUseCase,
    private val loadingManager: LoadingManager,
    private val feedbackPopUpManager: FeedbackPopUpManager,
) : SoulViewModelHolder<
            SettingsCloudSharedListActions,
            SettingsCloudSharedListNavScope,
            SettingsCloudSharedListState>(
        initialState = SettingsCloudSharedListState(
            codeField = SoulTextFieldHolderImpl(
                id = CODE_FIELD_ID,
                isValid = { true },
                getLabel = { strings.sharedListCodeLabel },
                getError = { null },
                onChange = {
                    savedStateHandle[CODE_FIELD_ID] = it
                },
                initialValue = savedStateHandle.get<String>(CODE_FIELD_ID).orEmpty()
            )
        )
    ), SettingsCloudSharedListActions {
    override val actions: SettingsCloudSharedListActions = this

    override fun navigateBack() {
        navigate { navigateBack() }
    }

    override fun join() {
        loadingManager.withLoadingOnScope(viewModelScope) {
            when (val result = joinSharedPlayedListUseCase(code = currentState.codeField.value.trim())) {
                is SoulResult.Error -> feedbackPopUpManager.showErrorIfAny(result)
                is SoulResult.Success -> currentState.codeField.onValueChanged("")
            }
        }
    }

    @Composable
    override fun Content(
        actions: SettingsCloudSharedListActions,
        state: SettingsCloudSharedListState
    ) {
        SettingsCloudSharedListScreen(
            actions = actions,
            state = state,
        )
    }

    private companion object {
        const val CODE_FIELD_ID = "CODE_FIELD_ID"
    }
}