package com.github.enteraname74.soulsearching.feature.settings.cloud.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder
import com.github.enteraname74.soulsearching.feature.settings.cloud.state.SettingsCloudFormState
import com.github.enteraname74.soulsearching.feature.settings.cloud.state.SettingsCloudState

@Composable
fun SettingsCloudExpandSwitchContent(
    state: SettingsCloudState.Data,
    logInFormState: SettingsCloudFormState,
    signInFormState: SettingsCloudFormState,
    hostTextField: SoulTextFieldHolder,
    signIn: () -> Unit,
    logIn: () -> Unit,
    onLogOut: () -> Unit,
) {
    AnimatedContent(
        targetState = state.user,
        transitionSpec = { fadeIn().togetherWith(fadeOut()) },
    ) {
        when(state.user) {
            null -> {
                SettingsCloudConnectionView(
                    logInFormState = logInFormState,
                    signInFormState = signInFormState,
                    hostTextField = hostTextField,
                    logIn = logIn,
                    signIn = signIn,
                )
            }
            else -> {
                SettingsCloudConnectedView(
                    user = state.user,
                    onLogOut = onLogOut,
                )
            }
        }
    }
}