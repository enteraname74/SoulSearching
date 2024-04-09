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
import com.github.soulsearching.domain.events.PlaylistEvent
import com.github.soulsearching.domain.model.types.BottomSheetStates
import com.github.soulsearching.domain.model.types.PlaylistType
import com.github.soulsearching.domain.viewmodel.AllImageCoversViewModel
import com.github.soulsearching.domain.viewmodel.AllPlaylistsViewModel
import com.github.soulsearching.domain.viewmodel.PlayerMusicListViewModel
import com.github.soulsearching.domain.viewmodel.PlayerViewModel
import com.github.soulsearching.domain.viewmodel.SelectedPlaylistViewModel
import com.github.soulsearching.elementpage.playlistpage.presentation.composable.PlaylistScreen
import com.github.soulsearching.mainpage.domain.state.PlaylistState
import com.github.soulsearching.modifyelement.modifymusic.presentation.ModifyMusicScreen
import com.github.soulsearching.modifyelement.modifyplaylist.presentation.ModifyPlaylistScreen
import java.util.UUID

/**
 * Represent the view of the selected album screen.
 */
@OptIn(ExperimentalMaterialApi::class)
data class SelectedPlaylistScreen(
    private val selectedPlaylistId: String
): Screen {

    @Composable
    override fun Content() {
        val screenModel = getScreenModel<SelectedPlaylistViewModel>()
        val allPlaylistsViewModel = getScreenModel<AllPlaylistsViewModel>()
        val allImagesViewModel = getScreenModel<AllImageCoversViewModel>()
        val playerMusicListViewModel = getScreenModel<PlayerMusicListViewModel>()

        val playerViewModel = getScreenModel<PlayerViewModel>()
        val playerDraggableState = playerViewModel.handler.playerDraggableState

        val playlistState by allPlaylistsViewModel.handler.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val colorThemeManager = injectElement<ColorThemeManager>()

        SelectedPlaylistScreenView(
            selectedPlaylistViewModel = screenModel,
            navigateToModifyMusic = { musicId ->
                navigator.push(
                    ModifyMusicScreen(
                        selectedMusicId = musicId
                    )
                )
            },
            selectedPlaylistId = selectedPlaylistId,
            playlistState = playlistState,
            onPlaylistEvent = allPlaylistsViewModel.handler::onPlaylistEvent,
            navigateToModifyPlaylist = {
                navigator.push(
                    ModifyPlaylistScreen(
                        selectedPlaylistId = selectedPlaylistId
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
fun SelectedPlaylistScreenView(
    onPlaylistEvent : (PlaylistEvent) -> Unit,
    playlistState : PlaylistState,
    selectedPlaylistViewModel: SelectedPlaylistViewModel,
    playerMusicListViewModel: PlayerMusicListViewModel,
    navigateToModifyPlaylist : (String) -> Unit,
    selectedPlaylistId : String,
    navigateToModifyMusic : (String) -> Unit,
    navigateBack : () -> Unit,
    retrieveCoverMethod: (UUID?) -> ImageBitmap?,
    playerDraggableState: SwipeableState<BottomSheetStates>
){
    var isPlaylistFetched by remember {
        mutableStateOf(false)
    }

    if (!isPlaylistFetched) {
        selectedPlaylistViewModel.handler.setSelectedPlaylist(UUID.fromString(selectedPlaylistId))
        isPlaylistFetched = true
    }

    val selectedPlaylistState by selectedPlaylistViewModel.handler.selectedPlaylistState.collectAsState()
    val musicState by selectedPlaylistViewModel.handler.musicState.collectAsState()

    PlaylistScreen(
        navigateBack = navigateBack,
        onMusicEvent = selectedPlaylistViewModel.handler::onMusicEvent,
        playlistState = playlistState,
        onPlaylistEvent = onPlaylistEvent,
        musicState = musicState,
        title = if (selectedPlaylistState.playlistWithMusics != null) selectedPlaylistState.playlistWithMusics!!.playlist.name else "",
        image = retrieveCoverMethod(selectedPlaylistState.playlistWithMusics?.playlist?.coverId),
        navigateToModifyPlaylist = {
            navigateToModifyPlaylist(selectedPlaylistId)
        },
        navigateToModifyMusic = navigateToModifyMusic,
        retrieveCoverMethod = { retrieveCoverMethod(it) },
        playerDraggableState = playerDraggableState,
        playlistId = selectedPlaylistState.playlistWithMusics?.playlist?.playlistId,
        playerMusicListViewModel = playerMusicListViewModel,
        updateNbPlayedAction = { selectedPlaylistViewModel.handler.onPlaylistEvent(PlaylistEvent.AddNbPlayed(it)) },
        playlistType = PlaylistType.PLAYLIST
    )
}