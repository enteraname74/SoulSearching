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
import com.github.soulsearching.colortheme.domain.model.ColorThemeManager
import com.github.soulsearching.domain.di.injectElement
import com.github.soulsearching.domain.model.types.BottomSheetStates
import com.github.soulsearching.domain.model.types.PlaylistType
import com.github.soulsearching.domain.viewmodel.AllImageCoversViewModel
import com.github.soulsearching.domain.viewmodel.AllPlaylistsViewModel
import com.github.soulsearching.domain.viewmodel.PlayerMusicListViewModel
import com.github.soulsearching.domain.viewmodel.PlayerViewModel
import com.github.soulsearching.domain.viewmodel.SelectedArtistViewModel
import com.github.soulsearching.elementpage.artistpage.domain.SelectedArtistEvent
import com.github.soulsearching.elementpage.playlistpage.presentation.composable.PlaylistScreen
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
        val playerMusicListViewModel = getScreenModel<PlayerMusicListViewModel>()

        val playerViewModel = getScreenModel<PlayerViewModel>()
        val playerDraggableState = playerViewModel.handler.playerDraggableState

        val navigator = LocalNavigator.currentOrThrow
        val colorThemeManager = injectElement<ColorThemeManager>()

        SelectedArtistScreenView(
            selectedArtistViewModel = screenModel,
            navigateToModifyMusic = { musicId ->
                navigator.push(
                    ModifyMusicScreen(
                        selectedMusicId = musicId
                    )
                )
            },
            selectedArtistId = selectedArtistId,
            navigateToModifyArtist = {
                navigator.push(
                    ModifyArtistScreen(
                        selectedArtistId = selectedArtistId
                    )
                )
            },
            navigateBack = {
                colorThemeManager.removePlaylistTheme()
                navigator.pop()
            },
            playerDraggableState = playerDraggableState,
            playerMusicListViewModel = playerMusicListViewModel,
            retrieveCoverMethod = allImagesViewModel.handler::getImageCover
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SelectedArtistScreenView(
    selectedArtistViewModel: SelectedArtistViewModel,
    playerMusicListViewModel: PlayerMusicListViewModel,
    selectedArtistId: String,
    navigateToModifyArtist: (String) -> Unit,
    navigateToModifyMusic: (String) -> Unit,
    navigateBack: () -> Unit,
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

    PlaylistScreen(
        navigateBack = navigateBack,
        title = state.artistWithMusics.artist.artistName,
        image = retrieveCoverMethod(state.artistWithMusics.artist.coverId),
        navigateToModifyPlaylist = {
            navigateToModifyArtist(selectedArtistId)
        },
        navigateToModifyMusic = navigateToModifyMusic,
        retrieveCoverMethod = { retrieveCoverMethod(it) },
        playerDraggableState = playerDraggableState,
        playlistId = state.artistWithMusics.artist.artistId,
        playerMusicListViewModel = playerMusicListViewModel,
        updateNbPlayedAction = {
            selectedArtistViewModel.handler.onEvent(
                SelectedArtistEvent.AddNbPlayed(
                    it
                )
            )
        },
        playlistType = PlaylistType.ARTIST,
        playlistWithMusics = state.allPlaylists,
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
        musics = state.artistWithMusics.musics
    )
}