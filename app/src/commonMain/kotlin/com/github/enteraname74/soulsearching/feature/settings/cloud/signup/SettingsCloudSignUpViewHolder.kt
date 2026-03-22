package com.github.enteraname74.soulsearching.feature.settings.cloud.signup

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.ImeAction
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
) : SoulViewModelHolder<
        SettingsCloudSignUpActions,
        SettingsCloudSignUpNavScope,
        SettingsCloudSignUpState,
        >(
    initialState = SettingsCloudSignUpState(
        nameField = SoulTextFieldHolderImpl(
            id = NAME_FIELD_ID,
            getLabel = { strings.cloudNameFieldLabel },
            getError = { strings.fieldCannotBeEmpty },
            style = SoulTextFieldStyle.Top,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
            )
        ),
        passwordField = SoulTextFieldHolderImpl(
            id = PASSWORD_FIELD_ID,
            getLabel = { strings.cloudPasswordFieldLabel },
            getError = { strings.fieldCannotBeEmpty },
            isPassword = true,
            onChange = { },
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
                    username = currentState.nameField.value,
                    password = currentState.passwordField.value,
                )
                println("CLUELESS -- result: $result")
                if (result.isError()) {
                    feedbackPopUpManager.showErrorIfAny(result)
                } else {
                    navigateBack()
                }
            }
        }
    }

    override fun navigateBack() {
        navigate { navigateBack() }
    }

    override fun toSignIn() {
        navigate { toSignIn() }
    }

    override val content: @Composable ((SettingsCloudSignUpActions, SettingsCloudSignUpState) -> Unit) =
        { actions, state ->
            SettingsCloudSignUpScreen(
                actions = actions,
                state = state,
            )
        }

    private companion object {
        const val NAME_FIELD_ID = "NAME_FIELD_ID"
        const val PASSWORD_FIELD_ID = "PASSWORD_FIELD_ID"
    }
}