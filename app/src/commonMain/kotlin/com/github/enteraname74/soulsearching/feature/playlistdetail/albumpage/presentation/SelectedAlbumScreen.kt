package com.github.enteraname74.soulsearching.feature.playlistdetail.albumpage.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.enteraname74.soulsearching.coreui.multiselection.SelectionMode
import com.github.enteraname74.soulsearching.coreui.screen.SoulErrorScreen
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.feature.playlistdetail.albumpage.domain.SelectedAlbumNavigationState
import com.github.enteraname74.soulsearching.feature.playlistdetail.albumpage.domain.SelectedAlbumState
import com.github.enteraname74.soulsearching.feature.playlistdetail.albumpage.domain.SelectedAlbumViewModel
import com.github.enteraname74.soulsearching.feature.playlistdetail.composable.PlaylistScreen

@Composable
fun SelectedAlbumRoute(
    viewModel: SelectedAlbumViewModel,
    onNavigationState: (SelectedAlbumNavigationState) -> Unit,
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

    SelectedAlbumScreenView(
        selectedAlbumViewModel = viewModel,
        navigateBack = viewModel::navigateBack,
    )
}

@Composable
fun SelectedAlbumScreenView(
    selectedAlbumViewModel: SelectedAlbumViewModel,
    navigateBack: () -> Unit,
) {
    val state by selectedAlbumViewModel.state.collectAsState()
    val multiSelectionState by selectedAlbumViewModel.multiSelectionState.collectAsState()

    when (state) {
        is SelectedAlbumState.Data -> PlaylistScreen(
            playlistDetail = (state as SelectedAlbumState.Data).playlistDetail,
            playlistDetailListener = selectedAlbumViewModel,
            navigateBack = navigateBack,
            onShowMusicBottomSheet = selectedAlbumViewModel::showMusicBottomSheet,
            multiSelectionManagerImpl = selectedAlbumViewModel.multiSelectionManagerImpl,
            onLongSelectOnMusic = {
                selectedAlbumViewModel.toggleElementInSelection(
                    id = it.musicId,
                    mode = SelectionMode.Music,
                )
            },
            multiSelectionState = multiSelectionState,
        )
        SelectedAlbumState.Loading -> SoulLoadingScreen(
            navigateBack = navigateBack,
        )

        SelectedAlbumState.Error -> {
            SoulErrorScreen(
                leftAction = TopBarNavigationAction(navigateBack),
                text = strings.albumDoesNotExists,
            )
        }
    }
}