package com.github.enteraname74.soulsearching.feature.settings.cloud.settings

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolderImpl
import com.github.enteraname74.soulsearching.domain.usecase.cloud.CommonCloudPreferencesUseCase
import com.github.enteraname74.soulsearching.domain.usecase.user.LogoutFromCloudUseCase
import com.github.enteraname74.soulsearching.viewholder.SoulViewModelHolderV2
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class SettingsCloudSettingsViewHolder(
    private val savedStateHandle: SavedStateHandle,
    private val commonCloudPreferencesUseCase: CommonCloudPreferencesUseCase,
    private val logoutFromCloudUseCase: LogoutFromCloudUseCase,
    private val loadingManager: LoadingManager,
) :
    SoulViewModelHolderV2<
        SettingsCloudSettingsNavScope,
        SettingsCloudSettingsState>() {

    init {
        viewModelScope.launch {
            val url = commonCloudPreferencesUseCase.observeUrl().firstOrNull()
            url?.let {
                currentState.urlField.onValueChanged(it)
            }
        }
    }

    @Composable
    override fun Content(state: SettingsCloudSettingsState) {
        SettingsCloudSettingsScreen(state)
    }

    override fun getInitialState(): SettingsCloudSettingsState =
        SettingsCloudSettingsState(
            urlField = SoulTextFieldHolderImpl(
                id = URL_FIELD_ID,
                isValid = { savedStateHandle.get<String>(URL_FIELD_ID)?.isBlank() != true },
                getLabel = { strings.cloudUrlFieldLabel },
                getError = { strings.fieldCannotBeEmpty },
                onChange = {
                    savedStateHandle[URL_FIELD_ID] = it
                },
                initialValue = savedStateHandle.get<String>(URL_FIELD_ID).orEmpty()
            ),
            saveChanges = ::saveChanges,
            navigateBack = { navigate { navigateBack() } },
        )

    private fun saveChanges() {
        viewModelScope.launch {
            val currentUrl = commonCloudPreferencesUseCase.observeUrl().firstOrNull()
            val updatedUrl = currentState.urlField.value.trim()

            if (currentState.urlField.isValid() && currentUrl != updatedUrl) {
                loadingManager.withLoading {
                    // If the remote URL differs from the current one, we log out the user
                    logoutFromCloudUseCase()
                    commonCloudPreferencesUseCase.setUrl(updatedUrl)
                }
            }
            navigate { navigateBack() }
        }
    }

    private companion object {
        const val URL_FIELD_ID = "URL_FIELD_ID"
    }
}
