package com.github.soulsearching.screens

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.github.soulsearching.classes.draggablestates.PlayerDraggableState
import com.github.soulsearching.classes.types.PlaylistType
import com.github.soulsearching.composables.PlaylistScreen
import com.github.soulsearching.events.AlbumEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.viewmodel.PlayerMusicListViewModel
import com.github.soulsearching.viewmodel.SelectedAlbumViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

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
    retrieveCoverMethod: (UUID?) -> Bitmap?,
    playerDraggableState: PlayerDraggableState
) {
    var isAlbumFetched by remember {
        mutableStateOf(false)
    }
    if (!isAlbumFetched) {
        selectedAlbumViewModel.setSelectedAlbum(UUID.fromString(selectedAlbumId))
        isAlbumFetched = true
    }

    val albumWithMusicsState by selectedAlbumViewModel.selectedAlbumState.collectAsState()
    val musicState by selectedAlbumViewModel.musicState.collectAsState()

    if (musicState.musics.isEmpty()) {
        SideEffect {
            CoroutineScope(Dispatchers.IO).launch {
                if (selectedAlbumViewModel.doesAlbumExists(UUID.fromString(selectedAlbumId))) {
                    withContext(Dispatchers.Main) {
                        navigateBack()
                    }
                }
            }
        }
    }
    PlaylistScreen(
        navigateBack = navigateBack,
        onPlaylistEvent = onPlaylistEvent,
        onMusicEvent = selectedAlbumViewModel::onMusicEvent,
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
        updateNbPlayedAction = { selectedAlbumViewModel.onAlbumEvent(AlbumEvent.AddNbPlayed(it)) },
        playlistType = PlaylistType.ALBUM
    )
}