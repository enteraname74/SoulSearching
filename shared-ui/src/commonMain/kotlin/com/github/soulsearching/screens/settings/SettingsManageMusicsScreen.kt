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
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.settings.SettingsElement
import com.github.soulsearching.strings
import com.github.soulsearching.theme.SoulSearchingColorTheme

@Composable
fun SettingsManageMusicsScreen(
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