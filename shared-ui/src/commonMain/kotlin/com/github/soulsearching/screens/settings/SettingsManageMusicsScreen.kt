package com.github.soulsearching.screens.settings

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
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.settings.SettingsElement
import com.github.soulsearching.events.AddMusicsEvent
import com.github.soulsearching.events.FolderEvent
import com.github.soulsearching.strings.strings
import com.github.soulsearching.theme.SoulSearchingColorTheme
import com.github.soulsearching.viewmodel.SettingsAddMusicsViewModel
import com.github.soulsearching.viewmodel.SettingsAllFoldersViewModel

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
                allFoldersViewModel.handler.onFolderEvent(
                    FolderEvent.FetchFolders
                )
                navigator.push(
                    SettingsUsedFoldersScreen()
                )
            },
            navigateToAddMusics = {
                addMusicsViewModel.handler.onAddMusicEvent(AddMusicsEvent.ResetState)
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
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoulSearchingColorTheme.colorScheme.primary)
    ) {
        AppHeaderBar(
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
        }
    }
}