package com.github.soulsearching.screens

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.github.soulsearching.composables.screens.PlaylistScreen
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.viewModels.SelectedArtistViewModel
import java.util.*

@Composable
fun SelectedArtistScreen(
    onPlaylistEvent : (PlaylistEvent) -> Unit,
    playlistState : PlaylistState,
    selectedArtistViewModel: SelectedArtistViewModel,
    selectedArtistId : String,
    navigateToModifyArtist : (String) -> Unit,
    navigateToModifyMusic: (String) -> Unit,
    navigateBack: () -> Unit
) {
    var isArtistFetched by rememberSaveable {
        mutableStateOf(false)
    }
    if (!isArtistFetched) {
        selectedArtistViewModel.setSelectedArtist(UUID.fromString(selectedArtistId))
        isArtistFetched = true
    }

    val artistWithMusicsState by selectedArtistViewModel.selectedArtistState.collectAsState()
    val musicState by selectedArtistViewModel.musicState.collectAsState()

    PlaylistScreen(
        navigateBack = navigateBack,
        playlistState = playlistState,
        onPlaylistEvent = onPlaylistEvent,
        onMusicEvent = selectedArtistViewModel::onMusicEvent,
        musicState = musicState,
        title = artistWithMusicsState.artistWithMusics.artist.artistName,
        image = artistWithMusicsState.artistWithMusics.artist.artistCover,
        navigateToModifyPlaylist = {
            navigateToModifyArtist(selectedArtistId)
        },
        navigateToModifyMusic = navigateToModifyMusic
    )
}