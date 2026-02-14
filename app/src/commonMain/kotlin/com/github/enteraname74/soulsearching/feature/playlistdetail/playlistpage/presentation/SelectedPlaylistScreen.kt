package com.github.enteraname74.soulsearching.feature.playlistdetail.playlistpage.presentation

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
import com.github.enteraname74.soulsearching.feature.playlistdetail.playlistpage.domain.SelectedPlaylistNavigationState
import com.github.enteraname74.soulsearching.feature.playlistdetail.playlistpage.domain.SelectedPlaylistState
import com.github.enteraname74.soulsearching.feature.playlistdetail.playlistpage.domain.SelectedPlaylistViewModel

@Composable
fun SelectedPlaylistRoute(
    viewModel: SelectedPlaylistViewModel,
    onNavigationState: (SelectedPlaylistNavigationState) -> Unit,
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

    SelectedPlaylistScreenView(
        selectedPlaylistViewModel = viewModel,
        navigateBack = viewModel::navigateBack,
    )
}

@Composable
fun SelectedPlaylistScreenView(
    selectedPlaylistViewModel: SelectedPlaylistViewModel,
    navigateBack: () -> Unit,
) {

    val state by selectedPlaylistViewModel.state.collectAsState()
    val multiSelectionState by selectedPlaylistViewModel.multiSelectionState.collectAsState()

    when (state) {
        SelectedPlaylistState.Loading -> SoulLoadingScreen(
            navigateBack = navigateBack,
        )
        is SelectedPlaylistState.Data -> PlaylistScreen(
            playlistDetail = (state as SelectedPlaylistState.Data).playlistDetail,
            playlistDetailListener = selectedPlaylistViewModel,
            navigateBack = navigateBack,
            onShowMusicBottomSheet = { selectedMusic ->
                selectedPlaylistViewModel.showMusicBottomSheetWithPlaylist(music = selectedMusic)
            },
            multiSelectionManagerImpl = selectedPlaylistViewModel.multiSelectionManagerImpl,
            onLongSelectOnMusic = {
                selectedPlaylistViewModel.toggleElementInSelection(
                    id = it.musicId,
                    mode = SelectionMode.Music,
                )
            },
            multiSelectionState = multiSelectionState,
        )

        SelectedPlaylistState.Error -> {
            SoulErrorScreen(
                leftAction = TopBarNavigationAction(navigateBack),
                text = strings.playlistDoesNotExists,
            )
        }
    }
}