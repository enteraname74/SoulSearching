package com.github.soulsearching.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.github.soulsearching.R
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.settings.SettingsSwitchElement
import com.github.soulsearching.ui.theme.DynamicColor
import com.github.soulsearching.viewModels.AllFoldersViewModel

@Composable
fun SettingsUsedFoldersScreen(
    finishAction : () -> Unit,
    allFoldersViewModel: AllFoldersViewModel
) {
    val folderState by allFoldersViewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DynamicColor.primary)
    ) {
        AppHeaderBar(
            title = stringResource(id = R.string.used_folders_title),
            leftAction = finishAction
        )
        LazyColumn {
            items(folderState.folders) {
                SettingsSwitchElement(
                    title = it.folderPath,
                    toggleAction = {
                        allFoldersViewModel.updateFolderState(it)
                    },
                    isChecked = it.isSelected
                )
            }
        }
    }
}