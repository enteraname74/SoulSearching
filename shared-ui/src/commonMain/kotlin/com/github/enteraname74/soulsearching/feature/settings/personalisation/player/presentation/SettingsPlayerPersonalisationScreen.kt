package com.github.enteraname74.soulsearching.feature.settings.personalisation.player.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuSwitch
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.settings.personalisation.player.domain.SettingsPlayerPersonalisationState
import com.github.enteraname74.soulsearching.feature.settings.personalisation.player.domain.SettingsPlayerPersonalisationViewModel
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage

/**
 * Represent the view of the player personalisation screen in the settings.
 */
class SettingsPlayerPersonalisationScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel: SettingsPlayerPersonalisationViewModel = koinScreenModel()
        val state: SettingsPlayerPersonalisationState by screenModel.state.collectAsState()

        SettingsPlayerPersonalisationScreenView(
            navigateBack = {
                navigator.pop()
            },
            state = state,
            togglePlayerSwipe = screenModel::togglePlayerSwipe,
            soulMixTextField = screenModel.soulMixTextField,
            toggleRewind = screenModel::toggleRewind,
            toggleMinimisedProgression = screenModel::toggleMinimisedProgression,
        )
    }

    @Composable
    private fun SettingsPlayerPersonalisationScreenView(
        navigateBack: () -> Unit,
        state: SettingsPlayerPersonalisationState,
        togglePlayerSwipe: () -> Unit,
        toggleRewind: () -> Unit,
        toggleMinimisedProgression: () -> Unit,
        soulMixTextField: SoulTextFieldHolder
    ) {

        val focusManager = LocalFocusManager.current

        SettingPage(
            navigateBack = navigateBack,
            title = strings.managePlayerTitle,
        ) {
            item {
                SoulMenuSwitch(
                    title = strings.playerSwipeTitle,
                    toggleAction = togglePlayerSwipe,
                    isChecked = state.isPlayerSwipeEnabled,
                    maxLines = 3,
                    padding = PaddingValues(
                        horizontal = UiConstants.Spacing.large,
                        vertical = UiConstants.Spacing.large,
                    ),
                )
            }
            item {
                SoulMenuSwitch(
                    title = strings.playerRewindTitle,
                    toggleAction = toggleRewind,
                    isChecked = state.isRewindEnabled,
                    maxLines = 3,
                    padding = PaddingValues(
                        horizontal = UiConstants.Spacing.large,
                        vertical = UiConstants.Spacing.large,
                    ),
                )
            }
            item {
                SoulMenuSwitch(
                    title = strings.playerMinimisedProgressionTitle,
                    toggleAction = toggleMinimisedProgression,
                    isChecked = state.isMinimisedSongProgressionShown,
                    maxLines = 3,
                    padding = PaddingValues(
                        horizontal = UiConstants.Spacing.large,
                        vertical = UiConstants.Spacing.large,
                    ),
                )
            }
            item {
                Column(
                    modifier = Modifier
                        .padding(
                            horizontal = UiConstants.Spacing.large,
                            vertical = UiConstants.Spacing.veryLarge,
                        )
                ) {
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