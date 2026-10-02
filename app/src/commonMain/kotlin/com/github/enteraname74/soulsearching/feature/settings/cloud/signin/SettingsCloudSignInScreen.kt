package com.github.enteraname74.soulsearching.feature.settings.cloud.signin

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulFilledButton
import com.github.enteraname74.soulsearching.coreui.button.SoulTextButton
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage

@Composable
fun SettingsCloudSignInScreen(
    actions: SettingsCloudSignInActions,
    state: SettingsCloudSignInState
) {
    val focusManager = LocalFocusManager.current

    SettingPage(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        focusManager.clearFocus()
                    }
                )
            },
        navigateBack = actions::navigateBack,
        contentPadding = PaddingValues(all = UiConstants.Spacing.large),
        title = strings.cloudConnection,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            state.nameField.TextField(
                modifier = Modifier
                    .widthIn(max = UiConstants.Size.textFieldMaxWidth)
                    .fillMaxWidth(),
                focusManager = focusManager,
            )
        }
        item {
            state.passwordField.TextField(
                modifier = Modifier
                    .widthIn(max = UiConstants.Size.textFieldMaxWidth)
                    .fillMaxWidth(),
                focusManager = focusManager,
            )
        }
        item {
            SoulFilledButton(
                modifier = Modifier
                    .padding(
                        top = UiConstants.Spacing.mediumPlus,
                    ),
                text = strings.cloudSignIn,
                enabled = state.isValid(),
                onClick = {
                    focusManager.clearFocus()
                    actions.signIn()
                },
            )
        }
        item {
            SoulTextButton(
                modifier = Modifier
                    .padding(
                        top = UiConstants.Spacing.medium,
                    ),
                text = strings.cloudNoAccount,
                onClick = {
                    focusManager.clearFocus()
                    actions.toSignUp()
                },
            )
        }

    }
}
