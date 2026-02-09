package com.github.enteraname74.soulsearching.feature.playlistdetail.artistpage.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.enteraname74.soulsearching.coreui.multiselection.SelectionMode
import com.github.enteraname74.soulsearching.coreui.screen.SoulErrorScreen
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.feature.playlistdetail.artistpage.domain.SelectedArtistNavigationState
import com.github.enteraname74.soulsearching.feature.playlistdetail.artistpage.domain.SelectedArtistState
import com.github.enteraname74.soulsearching.feature.playlistdetail.artistpage.domain.SelectedArtistViewModel
import com.github.enteraname74.soulsearching.feature.playlistdetail.artistpage.presentation.composable.ArtistAlbums
import com.github.enteraname74.soulsearching.feature.playlistdetail.composable.PlaylistScreen

@Composable
fun SelectedArtistRoute(
    viewModel: SelectedArtistViewModel,
    onNavigationState: (SelectedArtistNavigationState) -> Unit,
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

    SelectedArtistScreenView(
        selectedArtistViewModel = viewModel,
        navigateBack = viewModel::navigateBack,
    )
}

@Composable
fun SelectedArtistScreenView(
    selectedArtistViewModel: SelectedArtistViewModel,
    navigateBack: () -> Unit,
) {
    val state by selectedArtistViewModel.state.collectAsState()
    val multiSelectionState by selectedArtistViewModel.multiSelectionState.collectAsState()

    when (state) {
        is SelectedArtistState.Data -> {
            val dataState = (state as SelectedArtistState.Data)
            PlaylistScreen(
                playlistDetail = dataState.playlistDetail,
                playlistDetailListener = selectedArtistViewModel,
                navigateBack = navigateBack,
                onShowMusicBottomSheet = selectedArtistViewModel::showMusicBottomSheet,
                optionalContent = {
                    if (dataState.artistAlbums.isNotEmpty()) {
                        ArtistAlbums(
                            albums = dataState.artistAlbums,
                            onAlbumClick = selectedArtistViewModel::toAlbum,
                            onAlbumLongClick = { albumWithMusics ->
                                selectedArtistViewModel.toggleElementInSelection(
                                    id = albumWithMusics.album.albumId,
                                    mode = SelectionMode.Album,
                                )
                            },
                            multiSelectionState = multiSelectionState,
                        )
                    }
                },
                multiSelectionManagerImpl = selectedArtistViewModel.multiSelectionManagerImpl,
                onLongSelectOnMusic = {
                    selectedArtistViewModel.toggleElementInSelection(
                        id = it.musicId,
                        mode = SelectionMode.Music,
                    )
                },
                multiSelectionState = multiSelectionState,
            )
        }
        SelectedArtistState.Loading -> SoulLoadingScreen(
            navigateBack = navigateBack,
        )

        SelectedArtistState.Error -> {
            SoulErrorScreen(
                leftAction = TopBarNavigationAction(navigateBack),
                text = strings.artistDoesNotExists,
            )
        }
    }
}