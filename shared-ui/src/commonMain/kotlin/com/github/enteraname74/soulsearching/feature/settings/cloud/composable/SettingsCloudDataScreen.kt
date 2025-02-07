package com.github.enteraname74.soulsearching.feature.settings.cloud.composable

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.animation.VerticalAnimatedVisibility
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuExpandSwitch
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder
import com.github.enteraname74.soulsearching.feature.settings.cloud.SettingsCloudListener
import com.github.enteraname74.soulsearching.feature.settings.cloud.state.SettingsCloudLogInFormState
import com.github.enteraname74.soulsearching.feature.settings.cloud.state.SettingsCloudSignInFormState
import com.github.enteraname74.soulsearching.feature.settings.cloud.state.SettingsCloudState
import com.github.enteraname74.soulsearching.feature.settings.cloud.state.SettingsCloudUploadState
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage

@Composable
fun SettingsCloudDataScreen(
    state: SettingsCloudState.Data,
    uploadState: SettingsCloudUploadState,
    logInFormState: SettingsCloudLogInFormState,
    signInFormState: SettingsCloudSignInFormState,
    hostTextField: SoulTextFieldHolder,
    navigateBack: () -> Unit,
    listener: SettingsCloudListener,
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
                clickAction = listener::toggleCloudState,
                isExpanded = state.isCloudActivated,
            ) {
                SettingsCloudExpandSwitchContent(
                    logInFormState = logInFormState,
                    signInFormState = signInFormState,
                    hostTextField = hostTextField,
                    signIn = listener::signIn,
                    logIn = listener::logIn,
                    onLogOut = listener::logOut,
                    state = state,
                )
            }
        }

        if (state.user?.isAdmin == true) {
            item {
                VerticalAnimatedVisibility(
                    visible = state.user.isAdmin
                ) {
                    SettingsCloudGenerateCodeCard(
                        onGenerateCode = listener::generateCode,
                    )
                }
            }
        }

        item {
            SettingsCloudUploadCard(
                uploadSongs = listener::uploadAllMusicToCloud,
                uploadState = uploadState,
                isEnable = state.user != null,
                toggleSearchMetadata = listener::toggleSearchMetadata,
            )
        }
    }
}