package com.github.enteraname74.soulsearching.feature.settings.personalisation.player.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalFocusManager
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.settings.personalisation.player.domain.SettingsPlayerPersonalisationViewModel
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingsSwitchElement

/**
 * Represent the view of the player personalisation screen in the settings.
 */
class SettingsPlayerPersonalisationScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel: SettingsPlayerPersonalisationViewModel = getScreenModel()
        val isPlayerSwipeEnabled: Boolean by screenModel.isPlayerSwipeEnabled.collectAsState()

        SettingsPlayerPersonalisationScreenView(
            navigateBack = {
                navigator.pop()
            },
            isPlayerSwipeEnabled = isPlayerSwipeEnabled,
            togglePlayerSwipe = screenModel::togglePlayerSwipe,
            soulMixTextField = screenModel.soulMixTextField,
        )
    }

    @Composable
    private fun SettingsPlayerPersonalisationScreenView(
        navigateBack: () -> Unit,
        isPlayerSwipeEnabled: Boolean,
        togglePlayerSwipe: () -> Unit,
        soulMixTextField: SoulTextFieldHolder
    ) {

        val focusManager = LocalFocusManager.current

        SettingPage(
            navigateBack = navigateBack,
            title = strings.managePlayerTitle,
        ) {
            item {
                SettingsSwitchElement(
                    title = strings.playerSwipeTitle,
                    toggleAction = togglePlayerSwipe,
                    isChecked = isPlayerSwipeEnabled,
                    maxLines = 3
                )
            }
            item {
                Column {
                    Text(
                        text = strings.soulMixSettingsTitle,
                        style = UiConstants.Typography.bodyTitle,
                        color = SoulSearchingColorTheme.colorScheme.onPrimary,
                    )
                    soulMixTextField.TextField(focusManager = focusManager)
                }
            }
            item {
                SoulPlayerSpacer()
            }
        }
    }
}