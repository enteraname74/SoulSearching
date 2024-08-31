package com.github.enteraname74.soulsearching.feature.settings.personalisation.mainpage.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.domain.model.ElementsVisibility
import com.github.enteraname74.soulsearching.feature.settings.personalisation.mainpage.domain.SettingsMainPagePersonalisationState
import com.github.enteraname74.soulsearching.feature.settings.personalisation.mainpage.domain.SettingsMainPagePersonalisationViewModel
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingsSwitchElement

/**
 * Represent the view of the main page personalisation screen in the settings.
 */
class SettingsMainPagePersonalisationScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel: SettingsMainPagePersonalisationViewModel = getScreenModel()

        val state: SettingsMainPagePersonalisationState by screenModel.state.collectAsState()

        SettingsMainPagePersonalisationScreenView(
            navigateBack = { navigator.pop() },
            screenModel = screenModel,
            state = state,
        )
    }

    @Composable
    private fun SettingsMainPagePersonalisationScreenView(
        navigateBack: () -> Unit,
        screenModel: SettingsMainPagePersonalisationViewModel,
        state: SettingsMainPagePersonalisationState
    ) {
        when(state) {
            SettingsMainPagePersonalisationState.Loading -> {
                SoulLoadingScreen(navigateBack = navigateBack)
            }
            is SettingsMainPagePersonalisationState.Data -> {
                Data(
                    navigateBack = navigateBack,
                    screenModel = screenModel,
                    elementsVisibility = state.elementsVisibility
                )
            }
        }
    }

    @Composable
    private fun Data(
        navigateBack: () -> Unit,
        screenModel: SettingsMainPagePersonalisationViewModel,
        elementsVisibility: ElementsVisibility,
    ) {
        SettingPage(
            navigateBack = navigateBack,
            title = strings.mainPageTitle,
            contentPadding = PaddingValues(
                vertical = UiConstants.Spacing.large,
            )
        ) {
            item {
                SettingsSwitchElement(
                    title = strings.showQuickAccess,
                    toggleAction = { screenModel.toggleQuickAccessVisibility() },
                    isChecked = elementsVisibility.isQuickAccessShown,
                    padding = PaddingValues(
                        horizontal = UiConstants.Spacing.large,
                        vertical = UiConstants.Spacing.medium,
                    ),
                )
            }
            item {
                SettingsSwitchElement(
                    title = strings.showPlaylists,
                    toggleAction = { screenModel.togglePlaylistsVisibility() },
                    isChecked = elementsVisibility.arePlaylistsShown,
                    padding = PaddingValues(
                        horizontal = UiConstants.Spacing.large,
                        vertical = UiConstants.Spacing.medium,
                    ),
                )
            }
            item {
                SettingsSwitchElement(
                    title = strings.showAlbums,
                    toggleAction = { screenModel.toggleAlbumsVisibility() },
                    isChecked = elementsVisibility.areAlbumsShown,
                    padding = PaddingValues(
                        horizontal = UiConstants.Spacing.large,
                        vertical = UiConstants.Spacing.medium,
                    ),
                )
            }
            item {
                SettingsSwitchElement(
                    title = strings.showArtists,
                    toggleAction = { screenModel.toggleArtistsVisibility() },
                    isChecked = elementsVisibility.areArtistsShown,
                    padding = PaddingValues(
                        horizontal = UiConstants.Spacing.large,
                        vertical = UiConstants.Spacing.medium,
                    ),
                )
            }
            item {
                SettingsSwitchElement(
                    title = strings.showMusicsByFolders,
                    toggleAction = { screenModel.toggleMusicFoldersVisibility() },
                    isChecked = elementsVisibility.areMusicFoldersShown,
                    padding = PaddingValues(
                        horizontal = UiConstants.Spacing.large,
                        vertical = UiConstants.Spacing.medium,
                    ),
                )
            }
        }
    }
}

