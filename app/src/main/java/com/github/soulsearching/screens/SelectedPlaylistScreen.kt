package com.github.soulsearching.screens

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.github.soulsearching.composables.PlaylistScreen
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.viewModels.SelectedPlaylistViewModel
import java.util.*

@Composable
fun SelectedPlaylistScreen(
    onPlaylistEvent : (PlaylistEvent) -> Unit,
    playlistState : PlaylistState,
    selectedPlaylistViewModel: SelectedPlaylistViewModel,
    navigateToModifyPlaylist : (String) -> Unit,
    selectedPlaylistId : String,
    navigateToModifyMusic : (String) -> Unit,
    navigateBack : () -> Unit
){
    var isPlaylistFetched by rememberSaveable {
        mutableStateOf(false)
    }
    if (!isPlaylistFetched) {
        selectedPlaylistViewModel.setSelectedPlaylist(UUID.fromString(selectedPlaylistId))
        isPlaylistFetched = true
    }

    val selectedPlaylistState by selectedPlaylistViewModel.selectedPlaylistState.collectAsState()
    val musicState by selectedPlaylistViewModel.musicState.collectAsState()

    PlaylistScreen(
        navigateBack = navigateBack,
        onMusicEvent = selectedPlaylistViewModel::onMusicEvent,
        playlistState = playlistState,
        onPlaylistEvent = onPlaylistEvent,
        musicState = musicState,
        title = if (selectedPlaylistState.playlistWithMusics != null) selectedPlaylistState.playlistWithMusics!!.playlist.name else "",
        image = selectedPlaylistState.playlistWithMusics!!.playlist.playlistCover,
        navigateToModifyPlaylist = {
            navigateToModifyPlaylist(selectedPlaylistId)
        },
        navigateToModifyMusic = navigateToModifyMusic
    )
}