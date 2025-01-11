package com.github.enteraname74.soulsearching.feature.settings.cloud.composable

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuExpandSwitch
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder
import com.github.enteraname74.soulsearching.feature.settings.cloud.state.SettingsCloudFormState
import com.github.enteraname74.soulsearching.feature.settings.cloud.state.SettingsCloudState
import com.github.enteraname74.soulsearching.feature.settings.cloud.state.SettingsCloudUploadState
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage

@Composable
fun SettingsCloudDataScreen(
    state: SettingsCloudState.Data,
    uploadState: SettingsCloudUploadState,
    logInFormState: SettingsCloudFormState,
    signInFormState: SettingsCloudFormState,
    hostTextField: SoulTextFieldHolder,
    navigateBack: () -> Unit,
    toggleCloudMode: () -> Unit,
    signIn: () -> Unit,
    logIn: () -> Unit,
    onLogOut: () -> Unit,
    uploadSongs: () -> Unit,
    toggleSearchMetadata: () -> Unit,
) {
    SettingPage(
        navigateBack = navigateBack,
        title = strings.cloudSettingsTitle,
        contentPadding = PaddingValues(
            horizontal = UiConstants.Spacing.large,
        ),
        verticalPadding = UiConstants.Spacing.large,
    ) {
        item {
            SoulMenuExpandSwitch(
                title = strings.activateCloudMode,
                subTitle = null,
                clickAction = toggleCloudMode,
                isExpanded = state.isCloudActivated,
            ) {
                SettingsCloudExpandSwitchContent(
                    logInFormState = logInFormState,
                    signInFormState = signInFormState,
                    hostTextField = hostTextField,
                    signIn = signIn,
                    logIn = logIn,
                    onLogOut = onLogOut,
                    state = state,
                )
            }
        }
        item {
            SettingsCloudUploadCard(
                uploadSongs = uploadSongs,
                uploadState = uploadState,
                isEnable = state.user != null,
                toggleSearchMetadata = toggleSearchMetadata,
            )
        }
    }
}