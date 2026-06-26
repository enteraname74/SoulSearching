package com.github.enteraname74.soulsearching.feature.settings.cloud.signin

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulFilledButton
import com.github.enteraname74.soulsearching.coreui.button.SoulTextButton
import com.github.enteraname74.soulsearching.coreui.ext.toDp
import com.github.enteraname74.soulsearching.coreui.screen.SoulScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.coreui.utils.PlayerMinimisedHeight
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage

@Composable
fun SettingsCloudSignInScreen(
    actions: SettingsCloudSignInActions,
    state: SettingsCloudSignInState
) {
    val focusManager = LocalFocusManager.current

    SettingPage(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
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
    ) {
        item {
            state.nameField.TextField(
                modifier = Modifier.fillMaxWidth(),
                focusManager = focusManager,
            )
        }
        item {
            state.passwordField.TextField(
                modifier = Modifier.fillMaxWidth(),
                focusManager = focusManager,
            )
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = UiConstants.Spacing.mediumPlus,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                SoulFilledButton(
                    text = strings.cloudSignIn,
                    onClick = {
                        focusManager.clearFocus()
                        actions.signIn()
                    },
                )
            }
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = UiConstants.Spacing.mediumPlus,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                SoulTextButton(
                    text = strings.cloudNoAccount,
                    onClick = {
                        focusManager.clearFocus()
                        actions.toSignUp()
                    },
                )
            }
        }

    }
}