package com.github.enteraname74.soulsearching.feature.settings.managemusics.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuElement
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuSwitch
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.ViewSettingsManager
import com.github.enteraname74.soulsearching.ext.safePush
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation.SettingsAddMusicsScreen
import com.github.enteraname74.soulsearching.feature.settings.managemusics.managefolders.presentation.SettingsUsedFoldersScreen
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage

/**
 * Represent the view for managing musics and folders in the settings.
 */
class SettingsManageMusicsScreen : Screen, SettingPage {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        SettingsManageMusicsScreenView(
            finishAction = {
                navigator.pop()
            },
            navigateToFolders = {
                navigator.safePush(
                    SettingsUsedFoldersScreen()
                )
            },
            navigateToAddMusics = {
                navigator.safePush(
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
    SettingPage(
        navigateBack = finishAction,
        title = strings.manageMusicsTitle,
    ) {
        item {
            SoulMenuElement(
                title = strings.usedFoldersTitle,
                subTitle = strings.usedFoldersText,
                icon = Icons.Rounded.Folder,
                onClick = navigateToFolders
            )
        }
        item {
            SoulMenuElement(
                title = strings.addMusicsTitle,
                subTitle = strings.addMusicsText,
                icon = Icons.Rounded.MusicNote,
                onClick = navigateToAddMusics
            )
        }
        item {
            SoulMenuSwitch(
                title = strings.modifyMusicFileTitle,
                subTitle = strings.modifyMusicFileText,
                toggleAction = {
                    viewSettingsManager.toggleMusicFileModification()
                },
                isChecked = viewSettingsManager.isMusicFileModificationOn
            )
        }
    }
}