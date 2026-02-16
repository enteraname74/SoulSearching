package com.github.enteraname74.soulsearching.feature.settings.personalisation.mainpage.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulChoiceButtonData
import com.github.enteraname74.soulsearching.coreui.composable.SoulDivider
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuAction
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuDropdown
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuSwitch
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.ext.filledIcon
import com.github.enteraname74.soulsearching.ext.text
import com.github.enteraname74.soulsearching.feature.settings.personalisation.mainpage.domain.SettingsMainPagePersonalisationState
import com.github.enteraname74.soulsearching.feature.settings.personalisation.mainpage.domain.SettingsMainPagePersonalisationViewModel
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsMainPagePersonalisationRoute(
    navigateBack: () -> Unit,
) {
    val viewModel: SettingsMainPagePersonalisationViewModel = koinViewModel()
    val state: SettingsMainPagePersonalisationState by viewModel.state.collectAsState()

    SettingsMainPagePersonalisationScreenView(
        navigateBack = navigateBack,
        viewModel = viewModel,
        state = state,
    )
}

@Composable
private fun SettingsMainPagePersonalisationScreenView(
    navigateBack: () -> Unit,
    viewModel: SettingsMainPagePersonalisationViewModel,
    state: SettingsMainPagePersonalisationState
) {
    when(state) {
        SettingsMainPagePersonalisationState.Loading -> {
            SoulLoadingScreen(navigateBack = navigateBack)
        }
        is SettingsMainPagePersonalisationState.Data -> {
            Data(
                navigateBack = navigateBack,
                viewModel = viewModel,
                state = state,
                setShortcutAccessChoice = viewModel::setShortcutAccessChoice,
            )
        }
    }
}

@Composable
private fun Data(
    navigateBack: () -> Unit,
    viewModel: SettingsMainPagePersonalisationViewModel,
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
                toggleAction = { viewModel.toggleQuickAccessVisibility() },
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
                toggleAction = { viewModel.togglePlaylistsVisibility() },
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
                toggleAction = { viewModel.toggleAlbumsVisibility() },
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
                toggleAction = { viewModel.toggleArtistsVisibility() },
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
                toggleAction = { viewModel.toggleMusicFoldersVisibility() },
                isChecked = state.elementsVisibility.areMusicFoldersShown,
                padding = PaddingValues(
                    horizontal = UiConstants.Spacing.large,
                    vertical = UiConstants.Spacing.medium,
                ),
            )
        }
        item {
            SoulMenuDropdown(
                title = strings.initialSectionTitle,
                subTitle = strings.initialSectionText,
                padding = PaddingValues(
                    horizontal = UiConstants.Spacing.large,
                    vertical = UiConstants.Spacing.medium,
                ),
                choices = state.selectableTabs.map {
                    SoulChoiceButtonData(
                        icon = it.filledIcon(),
                        data = it,
                        title = it.text()
                    )
                },
                selectedChoiceName = state.initialTab.text(),
                onChoice = viewModel::setInitialTab,
            )
        }
        item {
            SoulDivider()
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

@Composable
private fun InitialTabSelection() {

}