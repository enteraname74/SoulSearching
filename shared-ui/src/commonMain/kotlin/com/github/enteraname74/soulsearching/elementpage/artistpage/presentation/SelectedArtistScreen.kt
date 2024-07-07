package com.github.soulsearching.elementpage.artistpage.presentation

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
import com.github.enteraname74.soulsearching.coreui.theme.color.ColorThemeManager
import com.github.soulsearching.domain.di.injectElement
import com.github.soulsearching.domain.model.types.BottomSheetStates
import com.github.soulsearching.elementpage.domain.PlaylistType
import com.github.soulsearching.domain.viewmodel.AllImageCoversViewModel
import com.github.soulsearching.domain.viewmodel.PlayerViewModel
import com.github.soulsearching.domain.viewmodel.SelectedArtistViewModel
import com.github.soulsearching.elementpage.albumpage.presentation.SelectedAlbumScreen
import com.github.soulsearching.elementpage.artistpage.domain.SelectedArtistEvent
import com.github.soulsearching.elementpage.artistpage.presentation.composable.ArtistScreen
import com.github.soulsearching.modifyelement.modifyalbum.presentation.ModifyAlbumScreen
import com.github.soulsearching.modifyelement.modifyartist.presentation.ModifyArtistScreen
import com.github.soulsearching.modifyelement.modifymusic.presentation.ModifyMusicScreen
import java.util.UUID

/**
 * Represent the view of the selected artist screen.
 */
@OptIn(ExperimentalMaterialApi::class)
data class SelectedArtistScreen(
    private val selectedArtistId: String
) : Screen {

    @Composable
    override fun Content() {
        val screenModel = getScreenModel<SelectedArtistViewModel>()
        val allImagesViewModel = getScreenModel<AllImageCoversViewModel>()

        val playerViewModel = getScreenModel<PlayerViewModel>()
        val playerDraggableState = playerViewModel.handler.playerDraggableState

        val navigator = LocalNavigator.currentOrThrow
        val colorThemeManager = injectElement<ColorThemeManager>()

        SelectedArtistScreenView(
            selectedArtistViewModel = screenModel,
            selectedArtistId = selectedArtistId,
            navigateToModifyArtist = {
                navigator.push(
                    ModifyArtistScreen(
                        selectedArtistId = selectedArtistId
                    )
                )
            },
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
            playerDraggableState = playerDraggableState,
            navigateToAlbum = { albumId ->
                navigator.push(
                    SelectedAlbumScreen(
                        selectedAlbumId = albumId
                    )
                )
            },
            navigateToModifyAlbum = { albumId ->
                navigator.push(
                    ModifyAlbumScreen(
                        selectedAlbumId = albumId
                    )
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Suppress("Deprecation")
fun SelectedArtistScreenView(
    selectedArtistViewModel: SelectedArtistViewModel,
    selectedArtistId: String,
    navigateToModifyArtist: (String) -> Unit,
    navigateToModifyMusic: (String) -> Unit,
    navigateToAlbum: (String) -> Unit,
    navigateBack: () -> Unit,
    navigateToModifyAlbum: (String) -> Unit,
    retrieveCoverMethod: (UUID?) -> ImageBitmap?,
    playerDraggableState: SwipeableState<BottomSheetStates>
) {
    var isArtistFetched by remember {
        mutableStateOf(false)
    }

    if (!isArtistFetched) {
        selectedArtistViewModel.handler.onEvent(
            SelectedArtistEvent.SetSelectedArtist(
                artistId = UUID.fromString(selectedArtistId)
            )
        )
        isArtistFetched = true
    }

    val state by selectedArtistViewModel.handler.state.collectAsState()

//    if (musicState.musics.isEmpty()) {
//        SideEffect {
//            CoroutineScope(Dispatchers.IO).launch {
//                if (!selectedArtistViewModel.handler.doesArtistExists(UUID.fromString(selectedArtistId))) {
//                    withContext(Dispatchers.Main) {
//                        navigateBack()
//                    }
//                }
//            }
//        }
//    }

    ArtistScreen(
        playlistId = state.artistWithMusics.artist.artistId,
        playlistWithMusics = state.allPlaylists,
        artistName = state.artistWithMusics.artist.artistName,
        artistCover = retrieveCoverMethod(state.artistWithMusics.artist.coverId),
        musics = state.artistWithMusics.musics,
        navigateToModifyArtist = {
            navigateToModifyArtist(selectedArtistId)
        },
        navigateToModifyMusic = navigateToModifyMusic,
        navigateBack = navigateBack,
        retrieveCoverMethod = { retrieveCoverMethod(it) },
        playerDraggableState = playerDraggableState,
        updateNbPlayedAction = {
            selectedArtistViewModel.handler.onEvent(
                SelectedArtistEvent.AddNbPlayed(
                    it
                )
            )
        },
        playlistType = PlaylistType.ARTIST,
        isDeleteMusicDialogShown = state.isDeleteMusicDialogShown,
        isBottomSheetShown = state.isMusicBottomSheetShown,
        isAddToPlaylistBottomSheetShown = state.isAddToPlaylistBottomSheetShown,
        onSetBottomSheetVisibility = { isShown ->
            selectedArtistViewModel.handler.onEvent(
                SelectedArtistEvent.SetMusicBottomSheetVisibility(
                    isShown = isShown
                )
            )
        },
        onSetDeleteMusicDialogVisibility = { isShown ->
            selectedArtistViewModel.handler.onEvent(
                SelectedArtistEvent.SetDeleteMusicDialogVisibility(
                    isShown = isShown
                )
            )
        },
        onSetAddToPlaylistBottomSheetVisibility = { isShown ->
            selectedArtistViewModel.handler.onEvent(
                SelectedArtistEvent.SetAddToPlaylistBottomSheetVisibility(
                    isShown = isShown
                )
            )
        },
        onDeleteMusic = { music ->
            selectedArtistViewModel.handler.onEvent(
                SelectedArtistEvent.DeleteMusic(
                    musicId = music.musicId
                )
            )
        },
        onToggleQuickAccessState = { music ->
            selectedArtistViewModel.handler.onEvent(
                SelectedArtistEvent.ToggleQuickAccessState(
                    musicId = music.musicId
                )
            )
        },
        onAddMusicToSelectedPlaylists = { selectedPlaylistsIds, selectedMusic ->
            selectedArtistViewModel.handler.onEvent(
                SelectedArtistEvent.AddMusicToPlaylists(
                    musicId = selectedMusic.musicId,
                    selectedPlaylistsIds = selectedPlaylistsIds
                )
            )
        },
        albums = state.artistAlbums,
        navigateToAlbum = navigateToAlbum,
        isDeleteAlbumDialogShown = state.isDeleteAlbumDialogShown,
        isAlbumBottomSheetShown = state.isAlbumBottomSheetShown,
        navigateToModifyAlbum = navigateToModifyAlbum,
        onSetAlbumBottomSheetVisibility = { isShown ->
            selectedArtistViewModel.handler.onEvent(
                SelectedArtistEvent.SetAlbumBottomSheetVisibility(
                    isShown = isShown
                )
            )
        },
        onSetDeleteAlbumDialogVisibility = { isShown ->
            selectedArtistViewModel.handler.onEvent(
                SelectedArtistEvent.SetDeleteAlbumDialogVisibility(
                    isShown = isShown
                )
            )
        },
        onToggleAlbumQuickAccessState = { album ->
            selectedArtistViewModel.handler.onEvent(
                SelectedArtistEvent.ToggleAlbumQuickAccessState(
                    album = album
                )
            )
        },
        onDeleteAlbum = { album ->
            selectedArtistViewModel.handler.onEvent(
                SelectedArtistEvent.DeleteAlbum(albumId = album.albumId)
            )
        }
    )
}