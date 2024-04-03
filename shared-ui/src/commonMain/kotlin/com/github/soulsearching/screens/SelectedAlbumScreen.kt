package com.github.soulsearching.screens

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
import com.github.soulsearching.composables.PlaylistScreen
import com.github.soulsearching.di.injectElement
import com.github.soulsearching.events.AlbumEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.theme.ColorThemeManager
import com.github.soulsearching.types.BottomSheetStates
import com.github.soulsearching.types.PlaylistType
import com.github.soulsearching.viewmodel.AllImageCoversViewModel
import com.github.soulsearching.viewmodel.AllPlaylistsViewModel
import com.github.soulsearching.viewmodel.PlayerMusicListViewModel
import com.github.soulsearching.viewmodel.SelectedAlbumViewModel
import java.util.UUID

/**
 * Represent the view of the selected album screen.
 */
@OptIn(ExperimentalMaterialApi::class)
data class SelectedAlbumScreen(
    private val selectedAlbumId: String,
    private val playerDraggableState: SwipeableState<BottomSheetStates>
): Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<SelectedAlbumViewModel>()
        val allPlaylistsViewModel = getScreenModel<AllPlaylistsViewModel>()
        val allImagesViewModel = getScreenModel<AllImageCoversViewModel>()
        val playerMusicListViewModel = getScreenModel<PlayerMusicListViewModel>()

        val playlistState by allPlaylistsViewModel.handler.state.collectAsState()
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
            playlistState = playlistState,
            onPlaylistEvent = allPlaylistsViewModel.handler::onPlaylistEvent,
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
    onPlaylistEvent : (PlaylistEvent) -> Unit,
    playlistState : PlaylistState,
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
        selectedAlbumViewModel.handler.setSelectedAlbum(UUID.fromString(selectedAlbumId))
        isAlbumFetched = true
    }

    val albumWithMusicsState by selectedAlbumViewModel.handler.selectedAlbumState.collectAsState()
    val musicState by selectedAlbumViewModel.handler.musicState.collectAsState()

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
        onPlaylistEvent = onPlaylistEvent,
        onMusicEvent = selectedAlbumViewModel.handler::onMusicEvent,
        playlistState = playlistState,
        musicState = musicState,
        title = albumWithMusicsState.albumWithMusics.album.albumName,
        image = retrieveCoverMethod(albumWithMusicsState.albumWithMusics.album.coverId),
        navigateToModifyPlaylist = {
            navigateToModifyAlbum(selectedAlbumId)
        },
        navigateToModifyMusic = navigateToModifyMusic,
        retrieveCoverMethod = { retrieveCoverMethod(it) },
        playerDraggableState = playerDraggableState,
        playlistId = albumWithMusicsState.albumWithMusics.album.albumId,
        playerMusicListViewModel = playerMusicListViewModel,
        updateNbPlayedAction = { selectedAlbumViewModel.handler.onAlbumEvent(AlbumEvent.AddNbPlayed(it)) },
        playlistType = PlaylistType.ALBUM
    )
}