package com.github.soulsearching.screens

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.github.soulsearching.composables.PlaylistScreen
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.viewModels.SelectedAlbumViewModel
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