package com.github.enteraname74.soulsearching.feature.settings.managemusics.managefolders.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.composable.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuSwitch
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarValidateAction
import com.github.enteraname74.soulsearching.feature.settings.managemusics.managefolders.domain.FolderState
import com.github.enteraname74.soulsearching.feature.settings.managemusics.managefolders.domain.SettingsAllFoldersViewModel
import com.github.enteraname74.soulsearching.feature.settings.managemusics.managefolders.presentation.composable.FolderStateComposable
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsUsedFoldersRoute(
    navigateBack: () -> Unit,
) {
    val viewModel = koinViewModel<SettingsAllFoldersViewModel>()

    val state: FolderState by viewModel.state.collectAsState()

    SettingsUsedFoldersScreenView(
        navigateBack = navigateBack,
        onSaveSelection = viewModel::saveSelection,
        state = state,
        setFolderSelectionStatus = viewModel::setFolderSelectionStatus,
    )
}

@Composable
fun SettingsUsedFoldersScreenView(
    state: FolderState,
    onSaveSelection: () -> Unit,
    setFolderSelectionStatus: (folder: Folder, isSelected: Boolean) -> Unit,
    navigateBack: () -> Unit,
) {
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
                onClick = onSaveSelection
            ),
        )
        when (state) {
            is FolderState.Data -> {
                LazyColumnCompat {
                    items(
                        items = state.folders,
                        key = { it.folderPath },
                        contentType = { USED_FOLDERS_CONTENT_TYPE }
                    ) {
                        SoulMenuSwitch(
                            title = it.folderPath,
                            toggleAction = {
                                setFolderSelectionStatus(it, !it.isSelected)
                            },
                            isChecked = it.isSelected,
                            padding = PaddingValues(
                                horizontal = UiConstants.Spacing.large,
                                vertical = UiConstants.Spacing.large,
                            ),
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
        }
    }
}

private const val USED_FOLDERS_CONTENT_TYPE: String = "USED_FOLDERS_CONTENT_TYPE"
private const val USED_FOLDERS_SPACER_KEY: String = "USED_FOLDERS_SPACER_KEY"
private const val USED_FOLDERS_SPACER_CONTENT_TYPE: String = "USED_FOLDERS_SPACER_CONTENT_TYPE"
