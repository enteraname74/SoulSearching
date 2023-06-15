package com.github.soulsearching.screens

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.github.soulsearching.classes.BottomSheetStates
import com.github.soulsearching.composables.PlaylistScreen
import com.github.soulsearching.database.model.ImageCover
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.viewModels.SelectedPlaylistViewModel
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SelectedPlaylistScreen(
    onPlaylistEvent : (PlaylistEvent) -> Unit,
    playlistState : PlaylistState,
    selectedPlaylistViewModel: SelectedPlaylistViewModel,
    navigateToModifyPlaylist : (String) -> Unit,
    selectedPlaylistId : String,
    navigateToModifyMusic : (String) -> Unit,
    navigateBack : () -> Unit,
    coverList: ArrayList<ImageCover>,
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
        image = coverList.find { it.coverId == selectedPlaylistState.playlistWithMusics!!.playlist.coverId }?.cover,
        navigateToModifyPlaylist = {
            navigateToModifyPlaylist(selectedPlaylistId)
        },
        navigateToModifyMusic = navigateToModifyMusic,
        coverList = coverList,
        swipeableState = swipeableState,
        playlistId = selectedPlaylistState.playlistWithMusics?.playlist?.playlistId
    )
}