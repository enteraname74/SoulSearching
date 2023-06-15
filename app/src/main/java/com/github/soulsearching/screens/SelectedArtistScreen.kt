package com.github.soulsearching.screens

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.github.soulsearching.classes.BottomSheetStates
import com.github.soulsearching.composables.PlaylistScreen
import com.github.soulsearching.database.model.ImageCover
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.ImageCoverState
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.viewModels.SelectedArtistViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SelectedArtistScreen(
    onPlaylistEvent : (PlaylistEvent) -> Unit,
    playlistState : PlaylistState,
    selectedArtistViewModel: SelectedArtistViewModel,
    selectedArtistId : String,
    navigateToModifyArtist : (String) -> Unit,
    navigateToModifyMusic: (String) -> Unit,
    navigateBack: () -> Unit,
    coverList: ArrayList<ImageCover>,
    swipeableState: SwipeableState<BottomSheetStates>
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

    if (musicState.musics.isEmpty()) {
        // On doit quand même regarder si l'artiste correspondant existe avant de revenir en arrière
        LaunchedEffect(key1 = "CheckIfAlbumDeleted") {
            CoroutineScope(Dispatchers.IO).launch {
                if (selectedArtistViewModel.checkIfArtistIdDeleted(UUID.fromString(selectedArtistId))) {
                    withContext(Dispatchers.Main) {
                        navigateBack()
                    }
                }
            }
        }
    }

    PlaylistScreen(
        navigateBack = navigateBack,
        playlistState = playlistState,
        onPlaylistEvent = onPlaylistEvent,
        onMusicEvent = selectedArtistViewModel::onMusicEvent,
        musicState = musicState,
        title = artistWithMusicsState.artistWithMusics.artist.artistName,
        image = coverList.find { it.coverId == artistWithMusicsState.artistWithMusics.artist.coverId }?.cover,
        navigateToModifyPlaylist = {
            navigateToModifyArtist(selectedArtistId)
        },
        navigateToModifyMusic = navigateToModifyMusic,
        coverList = coverList,
        swipeableState = swipeableState,
        playlistId = artistWithMusicsState.artistWithMusics.artist.artistId
    )
}