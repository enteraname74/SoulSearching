package com.github.soulsearching.screens

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.github.soulsearching.classes.draggablestates.PlayerDraggableState
import com.github.soulsearching.classes.enumsAndTypes.PlaylistType
import com.github.soulsearching.composables.PlaylistScreen
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.viewModels.PlayerMusicListViewModel
import com.github.soulsearching.viewModels.SelectedPlaylistViewModel
import java.util.UUID

@Composable
fun SelectedPlaylistScreen(
    onPlaylistEvent : (PlaylistEvent) -> Unit,
    playlistState : PlaylistState,
    selectedPlaylistViewModel: SelectedPlaylistViewModel,
    playerMusicListViewModel: PlayerMusicListViewModel,
    navigateToModifyPlaylist : (String) -> Unit,
    selectedPlaylistId : String,
    navigateToModifyMusic : (String) -> Unit,
    navigateBack : () -> Unit,
    retrieveCoverMethod: (UUID?) -> Bitmap?,
    playerDraggableState: PlayerDraggableState
){
    var isPlaylistFetched by remember {
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
        image = retrieveCoverMethod(selectedPlaylistState.playlistWithMusics?.playlist?.coverId),
        navigateToModifyPlaylist = {
            navigateToModifyPlaylist(selectedPlaylistId)
        },
        navigateToModifyMusic = navigateToModifyMusic,
        retrieveCoverMethod = { retrieveCoverMethod(it) },
        playerDraggableState = playerDraggableState,
        playlistId = selectedPlaylistState.playlistWithMusics?.playlist?.playlistId,
        playerMusicListViewModel = playerMusicListViewModel,
        updateNbPlayedAction = { selectedPlaylistViewModel.onPlaylistEvent(PlaylistEvent.AddNbPlayed(it)) },
        playlistType = PlaylistType.PLAYLIST
    )
}