package com.github.enteraname74.soulsearching.feature.settings.managemusics.managefolders.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarValidateAction
import com.github.enteraname74.soulsearching.feature.settings.managemusics.managefolders.domain.FolderEvent
import com.github.enteraname74.soulsearching.feature.settings.managemusics.managefolders.domain.SettingsAllFoldersViewModel
import com.github.enteraname74.soulsearching.feature.settings.managemusics.managefolders.domain.model.FolderStateType
import com.github.enteraname74.soulsearching.feature.settings.managemusics.managefolders.presentation.composable.FolderStateComposable
import com.github.enteraname74.soulsearching.feature.settings.managemusics.presentation.composable.LoadingComposable
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingsSwitchElement

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
}

@Composable
fun SettingsUsedFoldersScreenView(
    finishAction: () -> Unit,
    settingsAllFoldersViewModel: SettingsAllFoldersViewModel
) {
    val folderState by settingsAllFoldersViewModel.state.collectAsState()

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
        SoulTopBar(
            title = strings.usedFoldersTitle,
            leftAction = TopBarNavigationAction(
                onClick = finishAction,
            ),
            rightAction = TopBarValidateAction(
                isEnabled = folderState.state != FolderStateType.SAVING_SELECTION,
                onClick = {
                    settingsAllFoldersViewModel.onFolderEvent(
                        FolderEvent.SaveSelection(
                            updateProgress = {
                                savingProgress = it
                            }
                        )
                    )
                }
            ),
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
                                settingsAllFoldersViewModel.onFolderEvent(
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
                        SoulPlayerSpacer()
                    }
                }
            }
        }
    }
}