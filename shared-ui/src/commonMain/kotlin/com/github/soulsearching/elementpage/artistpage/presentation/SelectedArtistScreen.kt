package com.github.soulsearching.elementpage.artistpage.presentation

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.soulsearching.elementpage.playlistpage.presentation.composable.PlaylistScreen
import com.github.soulsearching.domain.di.injectElement
import com.github.soulsearching.domain.events.ArtistEvent
import com.github.soulsearching.domain.events.PlaylistEvent
import com.github.soulsearching.mainpage.domain.state.PlaylistState
import com.github.soulsearching.modifyelement.modifyartist.presentation.ModifyArtistScreen
import com.github.soulsearching.modifyelement.modifymusic.presentation.ModifyMusicScreen
import com.github.soulsearching.colortheme.domain.model.ColorThemeManager
import com.github.soulsearching.domain.model.types.BottomSheetStates
import com.github.soulsearching.domain.model.types.PlaylistType
import com.github.soulsearching.domain.viewmodel.AllImageCoversViewModel
import com.github.soulsearching.domain.viewmodel.AllPlaylistsViewModel
import com.github.soulsearching.domain.viewmodel.PlayerMusicListViewModel
import com.github.soulsearching.domain.viewmodel.SelectedArtistViewModel
import java.util.UUID

/**
 * Represent the view of the selected artist screen.
 */
@OptIn(ExperimentalMaterialApi::class)
data class SelectedArtistScreen(
    private val selectedArtistId: String,
    private val playerDraggableState: SwipeableState<BottomSheetStates>
): Screen {

    @Composable
    override fun Content() {
        val screenModel = getScreenModel<SelectedArtistViewModel>()
        val allPlaylistsViewModel = getScreenModel<AllPlaylistsViewModel>()
        val allImagesViewModel = getScreenModel<AllImageCoversViewModel>()
        val playerMusicListViewModel = getScreenModel<PlayerMusicListViewModel>()

        val playlistState by allPlaylistsViewModel.handler.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val colorThemeManager = injectElement<ColorThemeManager>()

        SelectedArtistScreenView(
            selectedArtistViewModel = screenModel,
            navigateToModifyMusic = { musicId ->
                navigator.push(
                    ModifyMusicScreen(
                        selectedMusicId = musicId
                    )
                )
            },
            selectedArtistId = selectedArtistId,
            playlistState = playlistState,
            onPlaylistEvent = allPlaylistsViewModel.handler::onPlaylistEvent,
            navigateToModifyArtist = {
                navigator.push(
                    ModifyArtistScreen(
                        selectedArtistId = selectedArtistId
                    )
                )
            },
            navigateBack = {
                colorThemeManager.removePlaylistTheme()
                navigator.pop()
            },
            playerDraggableState = playerDraggableState,
            playerMusicListViewModel = playerMusicListViewModel,
            retrieveCoverMethod = allImagesViewModel.handler::getImageCover
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SelectedArtistScreenView(
    onPlaylistEvent : (PlaylistEvent) -> Unit,
    playlistState : PlaylistState,
    selectedArtistViewModel: SelectedArtistViewModel,
    playerMusicListViewModel: PlayerMusicListViewModel,
    selectedArtistId : String,
    navigateToModifyArtist : (String) -> Unit,
    navigateToModifyMusic: (String) -> Unit,
    navigateBack: () -> Unit,
    retrieveCoverMethod: (UUID?) -> ImageBitmap?,
    playerDraggableState: SwipeableState<BottomSheetStates>
) {
    var isArtistFetched by remember {
        mutableStateOf(false)
    }

    if (!isArtistFetched) {
        selectedArtistViewModel.handler.setSelectedArtist(UUID.fromString(selectedArtistId))
        isArtistFetched = true
    }

    val artistWithMusicsState by selectedArtistViewModel.handler.selectedArtistState.collectAsState()
    val musicState by selectedArtistViewModel.handler.musicState.collectAsState()

//    if (musicState.musics.isEmpty()) {
//        SideEffect {
//            CoroutineScope(Dispatchers.IO).launch {
//                if (!selectedArtistViewModel.handler.doesArtistExists(UUID.fromString(selectedArtistId))) {
//                    withContext(Dispatchers.Main) {
//                        navigateBack()
//                    }
//                }
//            }
//        }
//    }

    PlaylistScreen(
        navigateBack = navigateBack,
        playlistState = playlistState,
        onPlaylistEvent = onPlaylistEvent,
        onMusicEvent = selectedArtistViewModel.handler::onMusicEvent,
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
        updateNbPlayedAction = { selectedArtistViewModel.handler.onArtistEvent(ArtistEvent.AddNbPlayed(it)) },
        playlistType = PlaylistType.ARTIST
    )
}