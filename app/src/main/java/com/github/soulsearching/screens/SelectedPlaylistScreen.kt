package com.github.soulsearching.screens

import android.graphics.Bitmap
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.github.soulsearching.classes.enumsAndTypes.BottomSheetStates
import com.github.soulsearching.classes.enumsAndTypes.PlaylistType
import com.github.soulsearching.composables.PlaylistScreen
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.viewModels.PlayerMusicListViewModel
import com.github.soulsearching.viewModels.SelectedPlaylistViewModel
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
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
    swipeableState: SwipeableState<BottomSheetStates>
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
        image = retrieveCoverMethod(selectedPlaylistState.playlistWithMusics?.playlist?.coverId),
        navigateToModifyPlaylist = {
            navigateToModifyPlaylist(selectedPlaylistId)
        },
        navigateToModifyMusic = navigateToModifyMusic,
        retrieveCoverMethod = { retrieveCoverMethod(it) },
        playerSwipeableState = swipeableState,
        playlistId = selectedPlaylistState.playlistWithMusics?.playlist?.playlistId,
        playerMusicListViewModel = playerMusicListViewModel,
        updateNbPlayedAction = { selectedPlaylistViewModel.onPlaylistEvent(PlaylistEvent.AddNbPlayed(it)) },
        playlistType = PlaylistType.PLAYLIST
    )
}