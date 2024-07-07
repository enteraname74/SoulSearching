package com.github.soulsearching.settings.personalisation.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.soulsearching.composables.SouTopBar
import com.github.enteraname74.soulsearching.coreui.SoulPlayerSpacer
import com.github.soulsearching.domain.di.injectElement
import com.github.soulsearching.settings.domain.ViewSettingsManager
import com.github.soulsearching.settings.presentation.composable.SettingsSwitchElement
import com.github.enteraname74.soulsearching.coreui.strings.strings

/**
 * Represent the view of the music view personalisation screen in the settings.
 */
class SettingsMusicViewPersonalisationScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        SettingsMusicViewPersonalisationScreenView(
            onBack = {
                navigator.pop()
            }
        )
    }
}

@Composable
fun SettingsMusicViewPersonalisationScreenView(
    onBack: () -> Unit,
    viewSettingsManager: ViewSettingsManager = injectElement()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoulSearchingColorTheme.colorScheme.primary)
    ) {
        SouTopBar(
            title = strings.manageMusicsViewText,
            leftAction = onBack
        )
        LazyColumn {
            item {
                SettingsSwitchElement(
                    title = strings.showMusicsByFolders,
                    toggleAction = { viewSettingsManager.toggleMusicsByFoldersVisibility() },
                    isChecked = viewSettingsManager.areMusicsByFoldersShown,
                    titleFontSize = 16.sp,
                    padding = PaddingValues(
                        start = UiConstants.Spacing.veryLarge,
                        end = UiConstants.Spacing.veryLarge,
                        top = UiConstants.Spacing.veryLarge,
                        bottom = UiConstants.Spacing.medium
                    )
                )
            }
            item {
                SettingsSwitchElement(
                    title = strings.showMusicsByMonths,
                    toggleAction = { viewSettingsManager.toggleMusicsByMonthsVisibility() },
                    isChecked = viewSettingsManager.areMusicsByMonthsShown,
                    titleFontSize = 16.sp,
                    padding = PaddingValues(
                        start = UiConstants.Spacing.veryLarge,
                        end = UiConstants.Spacing.veryLarge,
                        top = UiConstants.Spacing.medium,
                        bottom = UiConstants.Spacing.medium
                    )
                )
            }
            item {
                SoulPlayerSpacer()
            }
        }
    }
}