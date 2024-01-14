package com.github.soulsearching.screens

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import com.github.soulsearching.composables.PlaylistScreen
import com.github.soulsearching.events.AlbumEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.types.BottomSheetStates
import com.github.soulsearching.types.PlaylistType
import com.github.soulsearching.viewmodel.PlayerMusicListViewModel
import com.github.soulsearching.viewmodel.SelectedAlbumViewModel
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SelectedAlbumScreen(
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