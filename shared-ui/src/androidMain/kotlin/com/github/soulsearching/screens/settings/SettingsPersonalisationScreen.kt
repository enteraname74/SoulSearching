package com.github.soulsearching.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.utils.SettingsUtils
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.PlayerSpacer
import com.github.soulsearching.composables.setting.SettingsElement
import com.github.soulsearching.composables.setting.SettingsSwitchElement
import com.github.soulsearching.theme.DynamicColor

@Composable
fun SettingsPersonalisationScreen(
    finishAction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DynamicColor.primary)
    ) {
        AppHeaderBar(
            title = stringResource(id = R.string.personalization_title),
            leftAction = finishAction
        )
        LazyColumn {
            item { 
                SettingsElement(
                    title = stringResource(id = R.string.main_page_title),
                    text = stringResource(id = R.string.main_page_text),
                    padding = PaddingValues(Constants.Spacing.large)
                )
            }
            item {
                SettingsSwitchElement(
                    title = stringResource(id = R.string.show_quick_access),
                    toggleAction = { SettingsUtils.settingsViewModel.toggleQuickAccessVisibility() },
                    isChecked = SettingsUtils.settingsViewModel.isQuickAccessShown,
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
                    title = stringResource(id = R.string.show_playlists),
                    toggleAction = { SettingsUtils.settingsViewModel.togglePlaylistsVisibility() },
                    isChecked = SettingsUtils.settingsViewModel.isPlaylistsShown,
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
                    title = stringResource(id = R.string.show_albums),
                    toggleAction = { SettingsUtils.settingsViewModel.toggleAlbumsVisibility() },
                    isChecked = SettingsUtils.settingsViewModel.isAlbumsShown,
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
                    title = stringResource(id = R.string.show_artists),
                    toggleAction = { SettingsUtils.settingsViewModel.toggleArtistsVisibility() },
                    isChecked = SettingsUtils.settingsViewModel.isArtistsShown,
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
                    title = stringResource(id = R.string.show_vertical_access_bar_title),
                    text = stringResource(id = R.string.show_vertical_access_bar_text),
                    toggleAction = { SettingsUtils.settingsViewModel.toggleVerticalBarVisibility() },
                    isChecked = SettingsUtils.settingsViewModel.isVerticalBarShown,
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