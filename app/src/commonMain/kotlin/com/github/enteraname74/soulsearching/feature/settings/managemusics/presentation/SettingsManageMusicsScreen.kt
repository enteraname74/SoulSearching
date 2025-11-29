package com.github.enteraname74.soulsearching.feature.settings.managemusics.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuElement
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuSwitch
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.ViewSettingsManager
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage

@Composable
fun SettingsManageMusicsRoute(
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