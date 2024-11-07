package com.github.enteraname74.soulsearching.feature.settings.personalisation.mainpage.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuAction
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuSwitch
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.settings.personalisation.mainpage.domain.SettingsMainPagePersonalisationState
import com.github.enteraname74.soulsearching.feature.settings.personalisation.mainpage.domain.SettingsMainPagePersonalisationViewModel
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage

/**
 * Represent the view of the main page personalisation screen in the settings.
 */
class SettingsMainPagePersonalisationScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel: SettingsMainPagePersonalisationViewModel = koinScreenModel()

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
                    state = state,
                    setShortcutAccessChoice = screenModel::setShortcutAccessChoice,
                )
            }
        }
    }

    @Composable
    private fun Data(
        navigateBack: () -> Unit,
        screenModel: SettingsMainPagePersonalisationViewModel,
        state: SettingsMainPagePersonalisationState.Data,
        setShortcutAccessChoice: (Boolean) -> Unit,
    ) {
        SettingPage(
            navigateBack = navigateBack,
            title = strings.mainPageTitle,
            contentPadding = PaddingValues(
                vertical = UiConstants.Spacing.large,
            )
        ) {
            item {
                SoulMenuSwitch(
                    title = strings.showQuickAccess,
                    toggleAction = { screenModel.toggleQuickAccessVisibility() },
                    isChecked = state.elementsVisibility.isQuickAccessShown,
                    padding = PaddingValues(
                        horizontal = UiConstants.Spacing.large,
                        vertical = UiConstants.Spacing.medium,
                    ),
                )
            }
            item {
                SoulMenuSwitch(
                    title = strings.showPlaylists,
                    toggleAction = { screenModel.togglePlaylistsVisibility() },
                    isChecked = state.elementsVisibility.arePlaylistsShown,
                    padding = PaddingValues(
                        horizontal = UiConstants.Spacing.large,
                        vertical = UiConstants.Spacing.medium,
                    ),
                )
            }
            item {
                SoulMenuSwitch(
                    title = strings.showAlbums,
                    toggleAction = { screenModel.toggleAlbumsVisibility() },
                    isChecked = state.elementsVisibility.areAlbumsShown,
                    padding = PaddingValues(
                        horizontal = UiConstants.Spacing.large,
                        vertical = UiConstants.Spacing.medium,
                    ),
                )
            }
            item {
                SoulMenuSwitch(
                    title = strings.showArtists,
                    toggleAction = { screenModel.toggleArtistsVisibility() },
                    isChecked = state.elementsVisibility.areArtistsShown,
                    padding = PaddingValues(
                        horizontal = UiConstants.Spacing.large,
                        vertical = UiConstants.Spacing.medium,
                    ),
                )
            }
            item {
                SoulMenuSwitch(
                    title = strings.showMusicsByFolders,
                    toggleAction = { screenModel.toggleMusicFoldersVisibility() },
                    isChecked = state.elementsVisibility.areMusicFoldersShown,
                    padding = PaddingValues(
                        horizontal = UiConstants.Spacing.large,
                        vertical = UiConstants.Spacing.medium,
                    ),
                )
            }
            item {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(vertical = UiConstants.Spacing.large),
                    thickness = 1.dp,
                    color = SoulSearchingColorTheme.colorScheme.subPrimaryText,
                )
            }
            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = UiConstants.Spacing.large),
                    verticalArrangement = Arrangement.spacedBy(
                        UiConstants.Spacing.medium,
                    )
                ) {
                    SoulMenuAction(
                        title = strings.useVerticalAccessBarTitle,
                        subTitle = null,
                        clickAction = { setShortcutAccessChoice(true) },
                        isSelected = state.isUsingVerticalAccessBar,
                        padding = PaddingValues.Absolute(),
                    )
                    SoulMenuAction(
                        title = strings.useHorizontalAccessBarText,
                        subTitle = null,
                        clickAction = { setShortcutAccessChoice(false) },
                        isSelected = !state.isUsingVerticalAccessBar,
                        padding = PaddingValues.Absolute(),
                    )
                }
            }
        }
    }
}

