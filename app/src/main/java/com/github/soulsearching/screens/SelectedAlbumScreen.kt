package com.github.soulsearching.screens

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.github.soulsearching.composables.PlaylistScreen
import com.github.soulsearching.database.model.Album
import com.github.soulsearching.database.model.AlbumWithMusics
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.viewModels.SelectedAlbumViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

@Composable
fun SelectedAlbumScreen(
    onPlaylistEvent : (PlaylistEvent) -> Unit,
    playlistState : PlaylistState,
    selectedAlbumViewModel: SelectedAlbumViewModel,
    selectedAlbumId: String,
    navigateToModifyAlbum: (String) -> Unit,
    navigateToModifyMusic: (String) -> Unit,
    navigateBack : () -> Unit
) {
    var isAlbumFetched by rememberSaveable {
        mutableStateOf(false)
    }
    if (!isAlbumFetched) {
        selectedAlbumViewModel.setSelectedAlbum(UUID.fromString(selectedAlbumId))
        isAlbumFetched = true
    }

    val albumWithMusicsState by selectedAlbumViewModel.selectedAlbumState.collectAsState()
    val musicState by selectedAlbumViewModel.musicState.collectAsState()

    if (musicState.musics.isEmpty()) {
        // On doit quand même regarder si l'album correspondant existe avant de revenir en arrière
        LaunchedEffect(key1 = "CheckIfAlbumDeleted") {
            CoroutineScope(Dispatchers.IO).launch {
                if (selectedAlbumViewModel.checkIfAlbumIsDeleted(UUID.fromString(selectedAlbumId))) {
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
        image = albumWithMusicsState.albumWithMusics.album.albumCover,
        navigateToModifyPlaylist = {
            navigateToModifyAlbum(selectedAlbumId)
        },
        navigateToModifyMusic = navigateToModifyMusic
    )
}