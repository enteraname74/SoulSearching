package com.github.enteraname74.soulsearching.feature.settings.managemusics.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingsElement
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain.AddMusicsEvent
import com.github.enteraname74.soulsearching.feature.settings.managemusics.managefolders.domain.FolderEvent
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.ViewSettingsManager
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain.SettingsAddMusicsViewModel
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation.SettingsAddMusicsScreen
import com.github.enteraname74.soulsearching.feature.settings.managemusics.managefolders.domain.SettingsAllFoldersViewModel
import com.github.enteraname74.soulsearching.feature.settings.managemusics.managefolders.presentation.SettingsUsedFoldersScreen
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingsSwitchElement

/**
 * Represent the view for managing musics and folders in the settings.
 */
class SettingsManageMusicsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val allFoldersViewModel = getScreenModel<SettingsAllFoldersViewModel>()
        val addMusicsViewModel = getScreenModel<SettingsAddMusicsViewModel>()

        SettingsManageMusicsScreenView(
            finishAction = {
                navigator.pop()
            },
            navigateToFolders = {
                allFoldersViewModel.onFolderEvent(
                    FolderEvent.FetchFolders
                )
                navigator.push(
                    SettingsUsedFoldersScreen()
                )
            },
            navigateToAddMusics = {
                addMusicsViewModel.onAddMusicEvent(AddMusicsEvent.ResetState)
                navigator.push(
                    SettingsAddMusicsScreen()
                )
            }
        )
    }
}

@Composable
fun SettingsManageMusicsScreenView(
    finishAction: () -> Unit,
    navigateToFolders: () -> Unit,
    navigateToAddMusics: () -> Unit,
    viewSettingsManager: ViewSettingsManager = injectElement()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoulSearchingColorTheme.colorScheme.primary)
    ) {
        SoulTopBar(
            title = strings.manageMusicsTitle,
            leftAction = finishAction
        )
        LazyColumn {
            item {
                SettingsElement(
                    title = strings.usedFoldersTitle,
                    text = strings.usedFoldersText,
                    icon = Icons.Rounded.Folder,
                    clickAction = navigateToFolders
                )
            }
            item {
                SettingsElement(
                    title = strings.addMusicsTitle,
                    text = strings.addMusicsText,
                    icon = Icons.Rounded.MusicNote,
                    clickAction = navigateToAddMusics
                )
            }
            item {
                SettingsSwitchElement(
                    title = strings.modifyMusicFileTitle,
                    text = strings.modifyMusicFileText,
                    toggleAction = {
                        viewSettingsManager.toggleMusicFileModification()
                    },
                    isChecked = viewSettingsManager.isMusicFileModificationOn
                )
            }
            item {
                SoulPlayerSpacer()
            }
        }
    }
}