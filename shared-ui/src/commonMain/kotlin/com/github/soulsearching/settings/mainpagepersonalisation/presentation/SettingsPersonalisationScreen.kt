package com.github.soulsearching.settings.mainpagepersonalisation.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.soulsearching.Constants
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.PlayerSpacer
import com.github.soulsearching.settings.presentation.composable.SettingsElement
import com.github.soulsearching.settings.presentation.composable.SettingsSwitchElement
import com.github.soulsearching.domain.di.injectElement
import com.github.soulsearching.settings.domain.ViewSettingsManager
import com.github.soulsearching.strings.strings
import com.github.soulsearching.colortheme.domain.model.SoulSearchingColorTheme

/**
 * Represent the view of the personalisation screen in the settings.
 */
class SettingsPersonalisationScreen: Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        SettingsPersonalisationScreenView(
            finishAction = {
                navigator.pop()
            }
        )
    }
}

@Composable
fun SettingsPersonalisationScreenView(
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
                    padding = PaddingValues(Constants.Spacing.large),
                    isClickable = false
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
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = Constants.Spacing.large)
                )
            }
            item {
                SettingsElement(
                    title = strings.musics,
                    text = strings.manageMusicsViewText,
                    padding = PaddingValues(Constants.Spacing.large),
                    isClickable = false
                )
            }
            item {
                SettingsSwitchElement(
                    title = strings.showMusicsByFolders,
                    toggleAction = { viewSettingsManager.toggleMusicsByFoldersVisibility() },
                    isChecked = viewSettingsManager.areMusicsByFoldersShown,
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
                    title = strings.showMusicsByMonths,
                    toggleAction = { viewSettingsManager.toggleMusicsByMonthsVisibility() },
                    isChecked = viewSettingsManager.areMusicsByMonthsShown,
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
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = Constants.Spacing.large)
                )
            }
            item {
                SettingsElement(
                    title = strings.managePlayerTitle,
                    text = strings.managePlayerText,
                    padding = PaddingValues(Constants.Spacing.large),
                    isClickable = false
                )
            }
            item {
                SettingsSwitchElement(
                    title = strings.playerSwipeTitle,
                    toggleAction = { viewSettingsManager.togglePlayerSwipe() },
                    isChecked = viewSettingsManager.isPlayerSwipeEnabled,
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