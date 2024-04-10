package com.github.soulsearching.elementpage.albumpage.presentation

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.soulsearching.colortheme.domain.model.ColorThemeManager
import com.github.soulsearching.domain.di.injectElement
import com.github.soulsearching.domain.model.types.BottomSheetStates
import com.github.soulsearching.domain.model.types.PlaylistType
import com.github.soulsearching.domain.viewmodel.AllImageCoversViewModel
import com.github.soulsearching.domain.viewmodel.PlayerMusicListViewModel
import com.github.soulsearching.domain.viewmodel.PlayerViewModel
import com.github.soulsearching.domain.viewmodel.SelectedAlbumViewModel
import com.github.soulsearching.elementpage.albumpage.domain.SelectedAlbumEvent
import com.github.soulsearching.elementpage.playlistpage.presentation.composable.PlaylistScreen
import com.github.soulsearching.modifyelement.modifyalbum.presentation.ModifyAlbumScreen
import com.github.soulsearching.modifyelement.modifymusic.presentation.ModifyMusicScreen
import java.util.UUID

/**
 * Represent the view of the selected album screen.
 */
@OptIn(ExperimentalMaterialApi::class)
data class SelectedAlbumScreen(
    private val selectedAlbumId: String,
): Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<SelectedAlbumViewModel>()
        val allImagesViewModel = getScreenModel<AllImageCoversViewModel>()
        val playerMusicListViewModel = getScreenModel<PlayerMusicListViewModel>()
        val playerViewModel = getScreenModel<PlayerViewModel>()

        val playerDraggableState = playerViewModel.handler.playerDraggableState

        val navigator = LocalNavigator.currentOrThrow
        val colorThemeManager = injectElement<ColorThemeManager>()

        SelectedAlbumScreenView(
            selectedAlbumViewModel = screenModel,
            navigateToModifyAlbum = {
                navigator.push(
                    ModifyAlbumScreen(
                        selectedAlbumId = selectedAlbumId
                    )
                )
            },
            selectedAlbumId = selectedAlbumId,
            navigateToModifyMusic = { musicId ->
                navigator.push(
                    ModifyMusicScreen(
                        selectedMusicId = musicId
                    )
                )
            },
            navigateBack = {
                colorThemeManager.removePlaylistTheme()
                navigator.pop()
            },
            retrieveCoverMethod = allImagesViewModel.handler::getImageCover,
            playerMusicListViewModel = playerMusicListViewModel,
            playerDraggableState = playerDraggableState
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SelectedAlbumScreenView(
    selectedAlbumViewModel: SelectedAlbumViewModel,
    playerMusicListViewModel: PlayerMusicListViewModel,
    selectedAlbumId: String,
    navigateToModifyAlbum: (String) -> Unit,
    navigateToModifyMusic: (String) -> Unit,
    navigateBack : () -> Unit,
    retrieveCoverMethod: (UUID?) -> ImageBitmap?,
    playerDraggableState: SwipeableState<BottomSheetStates>
) {
    var isAlbumFetched by remember {
        mutableStateOf(false)
    }
    if (!isAlbumFetched) {
        selectedAlbumViewModel.handler.onEvent(
            SelectedAlbumEvent.SetSelectedAlbum(
                albumId = UUID.fromString(selectedAlbumId)
            )
        )
        isAlbumFetched = true
    }

    val state by selectedAlbumViewModel.handler.state.collectAsState()

//    if (musicState.musics.isEmpty()) {
//        SideEffect {
//            CoroutineScope(Dispatchers.IO).launch {
//                if (selectedAlbumViewModel.handler.doesAlbumExists(UUID.fromString(selectedAlbumId))) {
//                    withContext(Dispatchers.Main) {
//                        navigateBack()
//                    }
//                }
//            }
//        }
//    }
    PlaylistScreen(
        navigateBack = navigateBack,
        title = state.albumWithMusics.album.albumName,
        image = retrieveCoverMethod(state.albumWithMusics.album.coverId),
        navigateToModifyPlaylist = {
            navigateToModifyAlbum(selectedAlbumId)
        },
        navigateToModifyMusic = navigateToModifyMusic,
        retrieveCoverMethod = { retrieveCoverMethod(it) },
        playerDraggableState = playerDraggableState,
        playlistId = state.albumWithMusics.album.albumId,
        playerMusicListViewModel = playerMusicListViewModel,
        updateNbPlayedAction = { selectedAlbumViewModel.handler.onEvent(SelectedAlbumEvent.AddNbPlayed(it)) },
        playlistType = PlaylistType.ALBUM,
        playlistWithMusics = state.allPlaylists,
        isDeleteMusicDialogShown = state.isDeleteMusicDialogShown,
        isBottomSheetShown = state.isMusicBottomSheetShown,
        isAddToPlaylistBottomSheetShown = state.isAddToPlaylistBottomSheetShown,
        onSetBottomSheetVisibility = { isShown ->
            selectedAlbumViewModel.handler.onEvent(
                SelectedAlbumEvent.SetMusicBottomSheetVisibility(
                    isShown = isShown
                )
            )
        },
        onSetDeleteMusicDialogVisibility = { isShown ->
            selectedAlbumViewModel.handler.onEvent(
                SelectedAlbumEvent.SetDeleteMusicDialogVisibility(
                    isShown = isShown
                )
            )
        },
        onSetAddToPlaylistBottomSheetVisibility = { isShown ->
            selectedAlbumViewModel.handler.onEvent(
                SelectedAlbumEvent.SetAddToPlaylistBottomSheetVisibility(
                    isShown = isShown
                )
            )
        },
        onDeleteMusic = { music ->
            selectedAlbumViewModel.handler.onEvent(
                SelectedAlbumEvent.DeleteMusic(
                    musicId = music.musicId
                )
            )
        },
        onToggleQuickAccessState = { music ->
            selectedAlbumViewModel.handler.onEvent(
                SelectedAlbumEvent.ToggleQuickAccessState(
                    musicId = music.musicId
                )
            )
        },
        onAddMusicToSelectedPlaylists = { selectedPlaylistsIds, selectedMusic ->
            selectedAlbumViewModel.handler.onEvent(
                SelectedAlbumEvent.AddMusicToPlaylists(
                    musicId = selectedMusic.musicId,
                    selectedPlaylistsIds = selectedPlaylistsIds
                )
            )
        },
        musics = state.albumWithMusics.musics
    )
}