package com.github.soulsearching.elementpage.playlistpage.presentation

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
import com.github.soulsearching.domain.viewmodel.PlayerViewModel
import com.github.soulsearching.domain.viewmodel.SelectedPlaylistViewModel
import com.github.soulsearching.elementpage.playlistpage.domain.SelectedPlaylistEvent
import com.github.soulsearching.elementpage.playlistpage.presentation.composable.PlaylistScreen
import com.github.soulsearching.modifyelement.modifymusic.presentation.ModifyMusicScreen
import com.github.soulsearching.modifyelement.modifyplaylist.presentation.ModifyPlaylistScreen
import java.util.UUID

/**
 * Represent the view of the selected album screen.
 */
@OptIn(ExperimentalMaterialApi::class)
data class SelectedPlaylistScreen(
    private val selectedPlaylistId: String
) : Screen {

    @Composable
    override fun Content() {
        val screenModel = getScreenModel<SelectedPlaylistViewModel>()
        val allImagesViewModel = getScreenModel<AllImageCoversViewModel>()

        val playerViewModel = getScreenModel<PlayerViewModel>()
        val playerDraggableState = playerViewModel.handler.playerDraggableState

        val navigator = LocalNavigator.currentOrThrow
        val colorThemeManager = injectElement<ColorThemeManager>()

        SelectedPlaylistScreenView(
            selectedPlaylistViewModel = screenModel,
            navigateToModifyPlaylist = {
                navigator.push(
                    ModifyPlaylistScreen(
                        selectedPlaylistId = selectedPlaylistId
                    )
                )
            },
            selectedPlaylistId = selectedPlaylistId,
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
            playerDraggableState = playerDraggableState
        )
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SelectedPlaylistScreenView(
    selectedPlaylistViewModel: SelectedPlaylistViewModel,
    navigateToModifyPlaylist: (String) -> Unit,
    selectedPlaylistId: String,
    navigateToModifyMusic: (String) -> Unit,
    navigateBack: () -> Unit,
    retrieveCoverMethod: (UUID?) -> ImageBitmap?,
    playerDraggableState: SwipeableState<BottomSheetStates>
) {
    var isPlaylistFetched by remember {
        mutableStateOf(false)
    }

    if (!isPlaylistFetched) {
        selectedPlaylistViewModel.handler.onEvent(
            SelectedPlaylistEvent.SetSelectedPlaylist(
                playlistId = UUID.fromString(selectedPlaylistId)
            )
        )
        isPlaylistFetched = true
    }

    val state by selectedPlaylistViewModel.handler.state.collectAsState()

    PlaylistScreen(
        playlistId = state.playlistWithMusics?.playlist?.playlistId,
        playlistWithMusics = state.allPlaylists,
        playlistName = state.playlistWithMusics?.playlist?.name ?: "",
        playlistCover = retrieveCoverMethod(state.playlistWithMusics?.playlist?.coverId),
        musics = state.playlistWithMusics?.musics ?: emptyList(),
        navigateToModifyPlaylist = {
            navigateToModifyPlaylist(selectedPlaylistId)
        },
        navigateToModifyMusic = navigateToModifyMusic,
        navigateBack = navigateBack,
        retrieveCoverMethod = { retrieveCoverMethod(it) },
        playerDraggableState = playerDraggableState,
        updateNbPlayedAction = {
            selectedPlaylistViewModel.handler.onEvent(
                SelectedPlaylistEvent.AddNbPlayed(
                    it
                )
            )
        },
        playlistType = PlaylistType.PLAYLIST,
        isDeleteMusicDialogShown = state.isDeleteMusicDialogShown,
        isBottomSheetShown = state.isMusicBottomSheetShown,
        isAddToPlaylistBottomSheetShown = state.isAddToPlaylistBottomSheetShown,
        isRemoveFromPlaylistDialogShown = state.isRemoveFromPlaylistDialogShown,
        onSetBottomSheetVisibility = { isShown ->
            selectedPlaylistViewModel.handler.onEvent(
                SelectedPlaylistEvent.SetMusicBottomSheetVisibility(
                    isShown = isShown
                )
            )
        },
        onSetDeleteMusicDialogVisibility = { isShown ->
            selectedPlaylistViewModel.handler.onEvent(
                SelectedPlaylistEvent.SetDeleteMusicDialogVisibility(
                    isShown = isShown
                )
            )
        },
        onSetRemoveMusicFromPlaylistDialogVisibility = { isShown ->
            selectedPlaylistViewModel.handler.onEvent(
                SelectedPlaylistEvent.SetRemoveFromPlaylistDialogVisibility(
                    isShown = isShown
                )
            )
        },
        onSetAddToPlaylistBottomSheetVisibility = { isShown ->
            selectedPlaylistViewModel.handler.onEvent(
                SelectedPlaylistEvent.SetAddToPlaylistBottomSheetVisibility(
                    isShown = isShown
                )
            )
        },
        onDeleteMusic = { music ->
            selectedPlaylistViewModel.handler.onEvent(
                SelectedPlaylistEvent.DeleteMusic(
                    musicId = music.musicId
                )
            )
        },
        onToggleQuickAccessState = { music ->
            selectedPlaylistViewModel.handler.onEvent(
                SelectedPlaylistEvent.ToggleQuickAccessState(
                    musicId = music.musicId
                )
            )
        },
        onRemoveFromPlaylist = { music ->
            state.playlistWithMusics?.playlist?.playlistId?.let { playlistId ->
                selectedPlaylistViewModel.handler.onEvent(
                    SelectedPlaylistEvent.RemoveMusicFromPlaylist(
                        musicId = music.musicId,
                        playlistId = playlistId
                    )
                )
            }
        },
        onAddMusicToSelectedPlaylists = { selectedPlaylistsIds, selectedMusic ->
            selectedPlaylistViewModel.handler.onEvent(
                SelectedPlaylistEvent.AddMusicToPlaylists(
                    musicId = selectedMusic.musicId,
                    selectedPlaylistsIds = selectedPlaylistsIds
                )
            )
        }
    )
}