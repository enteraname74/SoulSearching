package com.github.soulsearching.screens.settings

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.github.soulsearching.R
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.PlayerSpacer
import com.github.soulsearching.composables.settings.FolderStateComposable
import com.github.soulsearching.composables.settings.LoadingComposable
import com.github.soulsearching.composables.settings.SettingsSwitchElement
import com.github.soulsearching.events.FolderEvent
import com.github.soulsearching.theme.SoulSearchingColorTheme
import com.github.soulsearching.types.FolderStateType
import com.github.soulsearching.viewmodel.SettingsAllFoldersViewModel

@Composable
fun SettingsUsedFoldersScreen(
    finishAction : () -> Unit,
    settingsAllFoldersViewModel: SettingsAllFoldersViewModel
) {
    val folderState by settingsAllFoldersViewModel.handler.state.collectAsState()

    var savingProgress by rememberSaveable {
        mutableFloatStateOf(0F)
    }
    val savingAnimatedProgress by animateFloatAsState(
        targetValue = savingProgress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = "SAVING_FOLDERS_SETTINGS_USED_FOLDERS_VIEW"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoulSearchingColorTheme.colorScheme.primary)
    ) {
        AppHeaderBar(
            title = stringResource(id = R.string.used_folders_title),
            leftAction = finishAction,
            rightIcon = if (folderState.state != FolderStateType.SAVING_SELECTION) Icons.Rounded.Check else null,
            rightAction = {
                settingsAllFoldersViewModel.handler.onFolderEvent(
                    FolderEvent.SaveSelection(
                        updateProgress = {
                            savingProgress = it
                        }
                    )
                )
            }
        )
        when(folderState.state) {
            FolderStateType.FETCHING_FOLDERS -> {
                FolderStateComposable(
                    stateTitle = stringResource(id = R.string.fetching_folders)
                )
            }
            FolderStateType.SAVING_SELECTION -> {
                LoadingComposable(
                    progressIndicator = savingAnimatedProgress,
                    progressMessage = stringResource(id = R.string.deleting_musics_from_unselected_folders)
                )
            }
            FolderStateType.WAITING_FOR_USER_ACTION -> {
                LazyColumn {
                    items(folderState.folders) {
                        SettingsSwitchElement(
                            title = it.folderPath,
                            toggleAction = {
                                settingsAllFoldersViewModel.handler.onFolderEvent(
                                    FolderEvent.SetSelectedFolder(
                                        folder = it,
                                        isSelected = !it.isSelected
                                    )
                                )
                            },
                            isChecked = it.isSelected
                        )
                    }
                    item {
                        PlayerSpacer()
                    }
                }
            }
        }
    }
}