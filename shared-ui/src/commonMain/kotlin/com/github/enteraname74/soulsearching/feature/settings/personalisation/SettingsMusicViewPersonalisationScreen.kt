package com.github.enteraname74.soulsearching.feature.settings.personalisation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.ViewSettingsManager
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingsSwitchElement

/**
 * Represent the view of the music view personalisation screen in the settings.
 */
class SettingsMusicViewPersonalisationScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        SettingsMusicViewPersonalisationScreenView(
            navigateBack = {
                navigator.pop()
            }
        )
    }

    @Composable
    private fun SettingsMusicViewPersonalisationScreenView(
        navigateBack: () -> Unit,
        viewSettingsManager: ViewSettingsManager = injectElement()
    ) {
        SettingPage(
            navigateBack = navigateBack,
            title = strings.manageMusicsTitle,
            verticalPadding = UiConstants.Spacing.large,
        ) {
            item {
                SettingsSwitchElement(
                    title = strings.showMusicsByMonths,
                    toggleAction = { viewSettingsManager.toggleMusicsByMonthsVisibility() },
                    isChecked = viewSettingsManager.areMusicsByMonthsShown
                )
            }
        }
    }
}