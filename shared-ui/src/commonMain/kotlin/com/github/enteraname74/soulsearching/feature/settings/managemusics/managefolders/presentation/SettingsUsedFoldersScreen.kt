package com.github.enteraname74.soulsearching.feature.settings.managemusics.managefolders.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.soulsearching.coreui.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarValidateAction
import com.github.enteraname74.soulsearching.feature.settings.managemusics.managefolders.domain.FolderState
import com.github.enteraname74.soulsearching.feature.settings.managemusics.managefolders.domain.SettingsAllFoldersViewModel
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

        val state: FolderState by screenModel.state.collectAsState()

        SettingsUsedFoldersScreenView(
            navigateBack = {
                navigator.pop()
            },
            onSaveSelection = screenModel::saveSelection,
            state = state,
            setFolderSelectionStatus = screenModel::setFolderSelectionStatus,
        )
    }
}

@Composable
fun SettingsUsedFoldersScreenView(
    state: FolderState,
    onSaveSelection: (updateProgress: (Float) -> Unit) -> Unit,
    setFolderSelectionStatus: (folder: Folder, isSelected: Boolean) -> Unit,
    navigateBack: () -> Unit,
) {

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
            leftAction = TopBarNavigationAction(onClick = navigateBack),
            rightAction = TopBarValidateAction(
                isEnabled = state is FolderState.Data,
                onClick = {
                    onSaveSelection {
                        savingProgress = it
                    }
                }
            ),
        )
        when (state) {
            is FolderState.Data -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.veryLarge),
                    contentPadding = PaddingValues(
                        all = UiConstants.Spacing.veryLarge,
                    )
                ) {
                    items(
                        items = state.folders,
                        key = { it.folderPath },
                        contentType = { USED_FOLDERS_CONTENT_TYPE }
                    ) {
                        SettingsSwitchElement(
                            title = it.folderPath,
                            toggleAction = {
                                setFolderSelectionStatus(it, !it.isSelected)
                            },
                            isChecked = it.isSelected
                        )
                    }
                    item(
                        key = USED_FOLDERS_SPACER_KEY,
                        contentType = USED_FOLDERS_SPACER_CONTENT_TYPE,
                    ) {
                        SoulPlayerSpacer()
                    }
                }
            }

            FolderState.Fetching -> {
                FolderStateComposable(
                    stateTitle = strings.fetchingFolders
                )
            }

            FolderState.Saving -> {
                LoadingComposable(
                    progressIndicator = savingAnimatedProgress,
                    progressMessage = strings.deletingMusicsFromUnselectedFolders
                )
            }
        }
    }
}

private const val USED_FOLDERS_CONTENT_TYPE: String = "USED_FOLDERS_CONTENT_TYPE"
private const val USED_FOLDERS_SPACER_KEY: String = "USED_FOLDERS_SPACER_KEY"
private const val USED_FOLDERS_SPACER_CONTENT_TYPE: String = "USED_FOLDERS_SPACER_CONTENT_TYPE"
