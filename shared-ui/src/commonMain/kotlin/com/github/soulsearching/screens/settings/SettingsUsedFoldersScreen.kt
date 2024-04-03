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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.PlayerSpacer
import com.github.soulsearching.composables.settings.FolderStateComposable
import com.github.soulsearching.composables.settings.LoadingComposable
import com.github.soulsearching.composables.settings.SettingsSwitchElement
import com.github.soulsearching.events.FolderEvent
import com.github.soulsearching.strings.strings
import com.github.soulsearching.theme.SoulSearchingColorTheme
import com.github.soulsearching.types.FolderStateType
import com.github.soulsearching.viewmodel.SettingsAllFoldersViewModel

/**
 * Represent the view of the used folders in the settings.
 */
class SettingsUsedFoldersScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<SettingsAllFoldersViewModel>()
        val navigator = LocalNavigator.currentOrThrow

        SettingsUsedFoldersScreenView(
            finishAction = {
                navigator.pop()
            },
            settingsAllFoldersViewModel = screenModel
        )
    }

    @Composable
    private fun SettingsUsedFoldersScreenView(
        finishAction: () -> Unit,
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
                title = strings.usedFoldersTitle,
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
            when (folderState.state) {
                FolderStateType.FETCHING_FOLDERS -> {
                    FolderStateComposable(
                        stateTitle = strings.fetchingFolders
                    )
                }

                FolderStateType.SAVING_SELECTION -> {
                    LoadingComposable(
                        progressIndicator = savingAnimatedProgress,
                        progressMessage = strings.deletingMusicsFromUnselectedFolders
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
}