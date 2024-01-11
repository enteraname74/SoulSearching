package com.github.soulsearching.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.github.soulsearching.Constants
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.PlayerSpacer
import com.github.soulsearching.composables.settings.SettingsElement
import com.github.soulsearching.composables.settings.SettingsSwitchElement
import com.github.soulsearching.di.injectElement
import com.github.soulsearching.model.settings.ViewSettingsManager
import com.github.soulsearching.strings
import com.github.soulsearching.theme.SoulSearchingColorTheme

@Composable
fun SettingsPersonalisationScreen(
    finishAction: () -> Unit,
    viewSettingsManager: ViewSettingsManager = injectElement()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoulSearchingColorTheme.colorScheme.primary)
    ) {
        AppHeaderBar(
            title = strings.personalizationTitle,
            leftAction = finishAction
        )
        LazyColumn {
            item { 
                SettingsElement(
                    title = strings.mainPageTitle,
                    text = strings.mainPageText,
                    padding = PaddingValues(Constants.Spacing.large)
                )
            }
            item {
                SettingsSwitchElement(
                    title = strings.showQuickAccess,
                    toggleAction = { viewSettingsManager.toggleQuickAccessVisibility() },
                    isChecked = viewSettingsManager.isQuickAccessShown,
                    titleFontSize = 16.sp,
                    padding = PaddingValues(
                        start = Constants.Spacing.veryLarge,
                        end = Constants.Spacing.veryLarge,
                        top = Constants.Spacing.medium,
                        bottom = Constants.Spacing.medium
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
                        start = Constants.Spacing.veryLarge,
                        end = Constants.Spacing.veryLarge,
                        top = Constants.Spacing.medium,
                        bottom = Constants.Spacing.medium
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
                        start = Constants.Spacing.veryLarge,
                        end = Constants.Spacing.veryLarge,
                        top = Constants.Spacing.medium,
                        bottom = Constants.Spacing.medium
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
                        start = Constants.Spacing.veryLarge,
                        end = Constants.Spacing.veryLarge,
                        top = Constants.Spacing.medium,
                        bottom = Constants.Spacing.medium
                    )
                )
            }
            item {
                SettingsSwitchElement(
                    title = strings.showVerticalAccessBarTitle,
                    text = strings.showVerticalAccessBarText,
                    toggleAction = { viewSettingsManager.toggleVerticalBarVisibility() },
                    isChecked = viewSettingsManager.isVerticalBarShown,
                    titleFontSize = 16.sp,
                    padding = PaddingValues(
                        start = Constants.Spacing.veryLarge,
                        end = Constants.Spacing.veryLarge,
                        top = Constants.Spacing.medium,
                        bottom = Constants.Spacing.medium
                    )
                )
            }
            item {
                PlayerSpacer()
            }
        }
    }
}