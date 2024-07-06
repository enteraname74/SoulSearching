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
 * Represent the view of the main page personalisation screen in the settings.
 */
class SettingsMainPagePersonalisationScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        SettingsMainPagePersonalisationScreenView(
            onBack = {
                navigator.pop()
            }
        )
    }
}

@Composable
fun SettingsMainPagePersonalisationScreenView(
    onBack: () -> Unit,
    viewSettingsManager: ViewSettingsManager = injectElement()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoulSearchingColorTheme.colorScheme.primary)
    ) {
        SouTopBar(
            title = strings.mainPageTitle,
            leftAction = onBack
        )
        LazyColumn {
            item {
                SettingsSwitchElement(
                    title = strings.showQuickAccess,
                    toggleAction = { viewSettingsManager.toggleQuickAccessVisibility() },
                    isChecked = viewSettingsManager.isQuickAccessShown,
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
                    title = strings.showPlaylists,
                    toggleAction = { viewSettingsManager.togglePlaylistsVisibility() },
                    isChecked = viewSettingsManager.isPlaylistsShown,
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
                SettingsSwitchElement(
                    title = strings.showAlbums,
                    toggleAction = { viewSettingsManager.toggleAlbumsVisibility() },
                    isChecked = viewSettingsManager.isAlbumsShown,
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
                SettingsSwitchElement(
                    title = strings.showArtists,
                    toggleAction = { viewSettingsManager.toggleArtistsVisibility() },
                    isChecked = viewSettingsManager.isArtistsShown,
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