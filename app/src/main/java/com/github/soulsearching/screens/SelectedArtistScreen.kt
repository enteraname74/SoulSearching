package com.github.soulsearching.screens

import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.github.soulsearching.classes.draggablestates.PlayerDraggableState
import com.github.soulsearching.classes.enumsAndTypes.PlaylistType
import com.github.soulsearching.composables.PlaylistScreen
import com.github.soulsearching.events.ArtistEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.viewModels.PlayerMusicListViewModel
import com.github.soulsearching.viewModels.SelectedArtistViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SelectedArtistScreen(
    onPlaylistEvent : (PlaylistEvent) -> Unit,
    playlistState : PlaylistState,
    selectedArtistViewModel: SelectedArtistViewModel,
    playerMusicListViewModel: PlayerMusicListViewModel,
    selectedArtistId : String,
    navigateToModifyArtist : (String) -> Unit,
    navigateToModifyMusic: (String) -> Unit,
    navigateBack: () -> Unit,
    retrieveCoverMethod: (UUID?) -> Bitmap?,
    playerDraggableState: PlayerDraggableState
) {
    var isArtistFetched by remember {
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
                if (selectedArtistViewModel.doesArtistExists(UUID.fromString(selectedArtistId))) {
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
        image = retrieveCoverMethod(artistWithMusicsState.artistWithMusics.artist.coverId),
        navigateToModifyPlaylist = {
            navigateToModifyArtist(selectedArtistId)
        },
        navigateToModifyMusic = navigateToModifyMusic,
        retrieveCoverMethod = { retrieveCoverMethod(it) },
        playerDraggableState = playerDraggableState,
        playlistId = artistWithMusicsState.artistWithMusics.artist.artistId,
        playerMusicListViewModel = playerMusicListViewModel,
        updateNbPlayedAction = { selectedArtistViewModel.onArtistEvent(ArtistEvent.AddNbPlayed(it)) },
        playlistType = PlaylistType.ARTIST
    )
}