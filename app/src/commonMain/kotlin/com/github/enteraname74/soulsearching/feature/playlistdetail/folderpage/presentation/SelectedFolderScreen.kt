package com.github.enteraname74.soulsearching.feature.playlistdetail.folderpage.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.enteraname74.soulsearching.coreui.multiselection.SelectionMode
import com.github.enteraname74.soulsearching.coreui.screen.SoulErrorScreen
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.feature.playlistdetail.composable.PlaylistScreen
import com.github.enteraname74.soulsearching.feature.playlistdetail.folderpage.domain.SelectedFolderNavigationState
import com.github.enteraname74.soulsearching.feature.playlistdetail.folderpage.domain.SelectedFolderState
import com.github.enteraname74.soulsearching.feature.playlistdetail.folderpage.domain.SelectedFolderViewModel

@Composable
fun SelectedFolderRoute(
    viewModel: SelectedFolderViewModel,
    onNavigationState: (SelectedFolderNavigationState) -> Unit,
) {
    val bottomSheetState by viewModel.bottomSheetState.collectAsState()
    val dialogState by viewModel.dialogState.collectAsState()
    val navigationState by viewModel.navigationState.collectAsState()
    val addToPlaylistBottomSheet by viewModel.addToPlaylistBottomSheet.collectAsState()

    bottomSheetState?.BottomSheet()
    dialogState?.Dialog()
    addToPlaylistBottomSheet?.BottomSheet()

    LaunchedEffect(navigationState) {
        onNavigationState(navigationState)
        viewModel.consumeNavigation()
    }

    SelectedFolderScreenView(
        selectedFolderViewModel = viewModel,
        navigateBack = viewModel::navigateBack,
    )
}

@Composable
fun SelectedFolderScreenView(
    selectedFolderViewModel: SelectedFolderViewModel,
    navigateBack: () -> Unit,
) {
    val state by selectedFolderViewModel.state.collectAsState()
    val multiSelectionState by selectedFolderViewModel.multiSelectionState.collectAsState()

    when (state) {
        is SelectedFolderState.Data -> PlaylistScreen(
            playlistDetail = (state as SelectedFolderState.Data).playlistDetail,
            playlistDetailListener = selectedFolderViewModel,
            navigateBack = navigateBack,
            onShowMusicBottomSheet = selectedFolderViewModel::showMusicBottomSheet,
            multiSelectionManagerImpl = selectedFolderViewModel.multiSelectionManagerImpl,
            onLongSelectOnMusic = {
                selectedFolderViewModel.toggleElementInSelection(
                    id = it.musicId,
                    mode = SelectionMode.Music,
                )
            },
            multiSelectionState = multiSelectionState,
        )
        SelectedFolderState.Loading -> SoulLoadingScreen(
            navigateBack = navigateBack,
        )

        SelectedFolderState.Error -> {
            SoulErrorScreen(
                leftAction = TopBarNavigationAction(navigateBack),
                text = strings.folderDoesNotExists,
            )
        }
    }
}
