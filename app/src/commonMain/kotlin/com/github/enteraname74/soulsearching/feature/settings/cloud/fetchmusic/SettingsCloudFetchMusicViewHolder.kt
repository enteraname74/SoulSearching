package com.github.enteraname74.soulsearching.feature.settings.cloud.fetchmusic

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.usecase.music.FetchMusicFromUrlUseCase
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolderImpl
import com.github.enteraname74.soulsearching.viewholder.SoulViewModelHolder

class SettingsCloudFetchMusicViewHolder(
    private val savedStateHandle: SavedStateHandle,
    private val fetchMusicFromUrlUseCase: FetchMusicFromUrlUseCase,
    private val loadingManager: LoadingManager,
    private val feedbackPopUpManager: FeedbackPopUpManager,
) : SoulViewModelHolder<
        SettingsCloudFetchMusicActions,
        SettingsCloudFetchMusicNavScope,
        SettingsCloudFetchMusicState>(
    initialState = SettingsCloudFetchMusicState(
        urlField = SoulTextFieldHolderImpl(
            id = URL_FIELD_ID,
            isValid = { it.isNotBlank() },
            getLabel = { strings.cloudFetchMusicFieldLabel },
            getError = { strings.fieldCannotBeEmpty },
            onChange = {
                savedStateHandle[URL_FIELD_ID] = it
            },
            initialValue = savedStateHandle.get<String>(URL_FIELD_ID).orEmpty()
        )
    )
), SettingsCloudFetchMusicActions {

    override val actions: SettingsCloudFetchMusicActions = this

    override fun navigateBack() {
        navigate { navigateBack() }
    }

    override fun fetch() {
        loadingManager.withLoadingOnScope(viewModelScope) {
            if (currentState.urlField.isValid()) {
                when (val result = fetchMusicFromUrlUseCase(url = currentState.urlField.value)) {
                    is SoulResult.Error -> feedbackPopUpManager.showErrorIfAny(result)
                    is SoulResult.Success -> currentState.urlField.onValueChanged("")
                }

            }
        }
    }

    @Composable
    override fun Content(
        actions: SettingsCloudFetchMusicActions,
        state: SettingsCloudFetchMusicState
    ) {
        SettingsCloudFetchMusicScreen(
            actions = actions,
            state = state,
        )
    }

    private companion object {
        const val URL_FIELD_ID = "URL_FIELD_ID"
    }
}