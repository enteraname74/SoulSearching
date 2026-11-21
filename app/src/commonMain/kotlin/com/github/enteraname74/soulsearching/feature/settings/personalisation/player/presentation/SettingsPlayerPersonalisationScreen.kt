package com.github.enteraname74.soulsearching.feature.settings.personalisation.player.presentation

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.composable.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuSwitch
import com.github.enteraname74.soulsearching.coreui.slider.SoulSlider
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.settings.personalisation.player.domain.SettingsPlayerPersonalisationState
import com.github.enteraname74.soulsearching.feature.settings.personalisation.player.domain.SettingsPlayerPersonalisationViewModel
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage
import org.koin.compose.viewmodel.koinViewModel
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec

@Composable
fun SettingsPlayerPersonalisationRoute(
    navigateBack: () -> Unit,
) {
    val viewModel: SettingsPlayerPersonalisationViewModel = koinViewModel()
    val state: SettingsPlayerPersonalisationState by viewModel.state.collectAsState()

    SettingsPlayerPersonalisationScreenView(
        navigateBack = navigateBack,
        state = state,
        togglePlayerSwipe = viewModel::togglePlayerSwipe,
        soulMixTextField = viewModel.soulMixTextField,
        toggleRewind = viewModel::toggleRewind,
        toggleMinimisedProgression = viewModel::toggleMinimisedProgression,
        setVolumePlayer = viewModel::setVolumePlayer,
    )
}
@Composable
private fun SettingsPlayerPersonalisationScreenView(
    navigateBack: () -> Unit,
    state: SettingsPlayerPersonalisationState,
    togglePlayerSwipe: () -> Unit,
    toggleRewind: () -> Unit,
    toggleMinimisedProgression: () -> Unit,
    setVolumePlayer: (Float) -> Unit,
    soulMixTextField: SoulTextFieldHolder
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
        navigateBack = navigateBack,
        title = CoreRes.string.manage_player_title.toTextSpec(),
    ) {
        item {
            SoulMenuSwitch(
                title = CoreRes.string.player_swipe_title.toTextSpec(),
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
                title = CoreRes.string.player_rewind_title.toTextSpec(),
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
                title = CoreRes.string.player_minimised_progression_title.toTextSpec(),
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
                    ),
                verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small),
            ) {
                SoulText(
                    text = CoreRes.string.soul_mix_settings_title.toTextSpec(),
                    style = UiConstants.Typography.bodyTitle,
                    color = SoulSearchingColorTheme.colorScheme.onPrimary,
                )
                soulMixTextField.TextField(
                    focusManager = focusManager,
                    modifier = Modifier,
                )
            }
        }
        item {
            Column(
                modifier = Modifier
                    .padding(
                        horizontal = UiConstants.Spacing.large,
                        vertical = UiConstants.Spacing.veryLarge,
                    ),
                verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small),
            ) {

                val uiVolume = state.playerVolume * 10

                SoulText(
                    text = CoreRes.string.player_volume.toTextSpec(),
                    style = UiConstants.Typography.bodyTitle,
                    color = SoulSearchingColorTheme.colorScheme.onPrimary,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        UiConstants.Spacing.small,
                    )
                ) {
                    SoulText(
                        text = "${uiVolume.toInt()}".toTextSpec(),
                        color = SoulSearchingColorTheme.colorScheme.onPrimary,
                        style = UiConstants.Typography.body,
                    )
                    SoulSlider(
                        modifier = Modifier
                            .fillMaxWidth(),
                        minValue = 0f,
                        maxValue = 10f,
                        value = uiVolume,
                        onThumbDragged = { playerVolume ->
                            playerVolume?.let {
                                val fixedVolume = (it / 10).coerceIn(0f, 1f)
                                setVolumePlayer(fixedVolume)
                            }
                        },
                        onValueChanged = {
                            // update done on drag
                        }
                    )
                }
            }
        }
        item {
            SoulPlayerSpacer()
        }
    }
}
