package com.github.enteraname74.soulsearching.feature.settings.cloud.signin

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
import com.github.enteraname74.soulsearching.coreui.ext.toDp
import com.github.enteraname74.soulsearching.coreui.screen.SoulScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.coreui.utils.PlayerMinimisedHeight

@Composable
fun SettingsCloudSignInScreen(
    actions: SettingsCloudSignInActions,
    state: SettingsCloudSignInState
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
                    )
                    .padding(
                        bottom = PlayerMinimisedHeight.toDp()
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
                    text = strings.cloudSignIn,
                    onClick = {
                        focusManager.clearFocus()
                        actions.signIn()
                    },
                )
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