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
import androidx.compose.ui.res.stringResource
import com.github.soulsearching.R
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.setting.SettingsElement
import com.github.soulsearching.ui.theme.DynamicColor

@Composable
fun SettingsManageMusicsScreen(
    finishAction: () -> Unit,
    navigateToFolders: () -> Unit,
    navigateToAddMusics: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DynamicColor.primary)
    ) {
        AppHeaderBar(
            title = stringResource(id = R.string.manage_musics_title),
            leftAction = finishAction
        )
        LazyColumn {
            item {
                SettingsElement(
                    title = stringResource(id = R.string.used_folders_title),
                    text = stringResource(id = R.string.used_folders_text),
                    icon = Icons.Rounded.Folder,
                    clickAction = navigateToFolders
                )
            }
            item {
                SettingsElement(
                    title = stringResource(id = R.string.add_musics_title),
                    text = stringResource(id = R.string.add_musics_text),
                    icon = Icons.Rounded.MusicNote,
                    clickAction = navigateToAddMusics
                )
            }
        }
    }
}