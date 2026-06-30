package com.github.enteraname74.soulsearching.feature.settings.cloud.signup

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.usecase.user.CommonUserUseCase
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolderImpl
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldStyle
import com.github.enteraname74.soulsearching.viewholder.SoulViewModelHolder
import kotlinx.coroutines.launch

class SettingsCloudSignUpViewHolder(
    private val commonUserUseCase: CommonUserUseCase,
    private val loadingManager: LoadingManager,
    private val feedbackPopUpManager: FeedbackPopUpManager,
    private val savedStateHandle: SavedStateHandle,
) :
    SoulViewModelHolder<
            SettingsCloudSignUpActions,
            SettingsCloudSignUpNavScope,
            SettingsCloudSignUpState,
            >(
        initialState = SettingsCloudSignUpState(
            nameField = SoulTextFieldHolderImpl(
                id = NAME_FIELD_ID,
                getLabel = { strings.cloudNameFieldLabel },
                getError = { strings.fieldCannotBeEmpty },
                isValid = { it.isNotBlank() },
                style = SoulTextFieldStyle.Top,
                initialValue = savedStateHandle.get<String>(NAME_FIELD_ID).orEmpty(),
                onChange = {
                    savedStateHandle[NAME_FIELD_ID] = it
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                )
            ),
            passwordField = SoulTextFieldHolderImpl(
                id = PASSWORD_FIELD_ID,
                getLabel = { strings.cloudPasswordFieldLabel },
                getError = { strings.fieldCannotBeEmpty },
                isValid = { it.isNotBlank() },
                isPassword = true,
                initialValue = savedStateHandle.get<String>(PASSWORD_FIELD_ID).orEmpty(),
                onChange = {
                    savedStateHandle[PASSWORD_FIELD_ID] = it
                },
                style = SoulTextFieldStyle.Body,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                )
            ),
            codeField = SoulTextFieldHolderImpl(
                id = CODE_FIELD_ID,
                getLabel = { strings.cloudRegistrationCode },
                getError = { strings.fieldCannotBeEmpty },
                isValid = { it.isNotBlank() },
                isPassword = true,
                initialValue = savedStateHandle.get<String>(CODE_FIELD_ID).orEmpty(),
                onChange = {
                    savedStateHandle[CODE_FIELD_ID] = it
                },
                style = SoulTextFieldStyle.Bottom,
            ),
        )
    ), SettingsCloudSignUpActions {
    override val actions: SettingsCloudSignUpActions = this

    override fun signUp() {
        if (!currentState.isValid()) return

        viewModelScope.launch {
            loadingManager.withLoading {
                val result: SoulResult<Unit> = commonUserUseCase.signUp(
                    username = currentState.nameField.value.trim(),
                    password = currentState.passwordField.value.trim(),
                    code = currentState.codeField.value.trim()
                )
                if (result.isError()) {
                    feedbackPopUpManager.showErrorIfAny(result)
                } else {
                    navigate { navigateBackToCloud() }
                }
            }
        }
    }

    override fun navigateBack() {
        navigate { navigateBack() }
    }

    @Composable
    override fun Content(
        actions: SettingsCloudSignUpActions,
        state: SettingsCloudSignUpState
    ) {
        SettingsCloudSignUpScreen(
            actions = actions,
            state = state,
        )
    }

    private companion object {
        const val NAME_FIELD_ID = "NAME_FIELD_ID"
        const val PASSWORD_FIELD_ID = "PASSWORD_FIELD_ID"
        const val CODE_FIELD_ID = "CODE_FIELD_ID"
    }
}