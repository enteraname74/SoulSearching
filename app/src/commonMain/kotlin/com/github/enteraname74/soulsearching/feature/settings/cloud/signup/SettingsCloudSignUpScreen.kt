package com.github.enteraname74.soulsearching.feature.settings.cloud.signup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
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
import com.github.enteraname74.soulsearching.coreui.screen.SoulScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction

@Composable
fun SettingsCloudSignUpScreen(
    actions: SettingsCloudSignUpActions,
    state: SettingsCloudSignUpState
) {
    SoulScreen {

        val focusManager = LocalFocusManager.current

        Column {
            SoulTopBar(
                title = strings.cloudConnection,
                leftAction = TopBarNavigationAction(
                    onClick = actions::navigateBack,
                ),
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                focusManager.clearFocus()
                            }
                        )
                    }
                    .padding(
                        all = UiConstants.Spacing.large,
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                state.nameField.TextField(
                    modifier = Modifier.fillMaxWidth(),
                    focusManager = focusManager,
                )
                state.passwordField.TextField(
                    modifier = Modifier.fillMaxWidth(),
                    focusManager = focusManager,
                )
                SoulFilledButton(
                    modifier = Modifier
                        .padding(
                            top = UiConstants.Spacing.mediumPlus,
                        ),
                    text = strings.cloudSignUp,
                    onClick = actions::signUp,
                )
                SoulTextButton(
                    text = strings.cloudNoAccount,
                    onClick = actions::toSignIn,
                )
            }
        }
    }
}