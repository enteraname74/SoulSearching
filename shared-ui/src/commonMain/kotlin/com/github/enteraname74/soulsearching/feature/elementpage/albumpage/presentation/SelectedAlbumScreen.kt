package com.github.enteraname74.soulsearching.feature.elementpage.albumpage.presentation

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.theme.color.ColorThemeManager
import com.github.enteraname74.soulsearching.domain.di.injectElement
import com.github.enteraname74.soulsearching.feature.coversprovider.AllImageCoversViewModel
import com.github.enteraname74.soulsearching.feature.elementpage.albumpage.domain.SelectedAlbumEvent
import com.github.enteraname74.soulsearching.feature.elementpage.albumpage.domain.SelectedAlbumViewModel
import com.github.enteraname74.soulsearching.feature.elementpage.albumpage.presentation.composable.AlbumScreen
import com.github.enteraname74.soulsearching.feature.elementpage.artistpage.presentation.SelectedArtistScreen
import com.github.enteraname74.soulsearching.feature.elementpage.domain.PlaylistType
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyalbum.presentation.ModifyAlbumScreen
import com.github.enteraname74.soulsearching.feature.modifyelement.modifymusic.presentation.ModifyMusicScreen
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerViewModel
import java.util.*

/**
 * Represent the view of the selected album screen.
 */
data class SelectedAlbumScreen(
    private val selectedAlbumId: String,
) : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<SelectedAlbumViewModel>()
        val allImagesViewModel = getScreenModel<AllImageCoversViewModel>()
        val playerViewModel = getScreenModel<PlayerViewModel>()

        val navigator = LocalNavigator.currentOrThrow
        val colorThemeManager = injectElement<ColorThemeManager>()

        val state by screenModel.state.collectAsState()

        SelectedAlbumScreenView(
            selectedAlbumViewModel = screenModel,
            selectedAlbumId = selectedAlbumId,
            navigateToModifyAlbum = {
                navigator.push(
                    ModifyAlbumScreen(
                        selectedAlbumId = selectedAlbumId
                    )
                )
            },
            navigateToModifyMusic = { musicId ->
                navigator.push(
                    ModifyMusicScreen(
                        selectedMusicId = musicId
                    )
                )
            },
            navigateToArtist = {
                state.albumWithMusics.artist?.let { artist ->
                    navigator.push(
                        SelectedArtistScreen(
                            selectedArtistId = artist.artistId.toString()
                        )
                    )
                }

            },
            navigateBack = {
                colorThemeManager.removePlaylistTheme()
                navigator.pop()
            },
            retrieveCoverMethod = allImagesViewModel::getImageCover,
        )
    }
}

@Composable
fun SelectedAlbumScreenView(
    selectedAlbumViewModel: SelectedAlbumViewModel,
    selectedAlbumId: String,
    navigateToModifyAlbum: (String) -> Unit,
    navigateToModifyMusic: (String) -> Unit,
    navigateToArtist: () -> Unit,
    navigateBack: () -> Unit,
    retrieveCoverMethod: (UUID?) -> ImageBitmap?,
) {
    var isAlbumFetched by remember {
        mutableStateOf(false)
    }
    if (!isAlbumFetched) {
        selectedAlbumViewModel.onEvent(
            SelectedAlbumEvent.SetSelectedAlbum(
                albumId = UUID.fromString(selectedAlbumId)
            )
        )
        isAlbumFetched = true
    }

    val state by selectedAlbumViewModel.state.collectAsState()

//    if (musicState.musics.isEmpty()) {
//        SideEffect {
//            CoroutineScope(Dispatchers.IO).launch {
//                if (selectedAlbumViewModel.doesAlbumExists(UUID.fromString(selectedAlbumId))) {
//                    withContext(Dispatchers.Main) {
//                        navigateBack()
//                    }
//                }
//            }
//        }
//    }
    AlbumScreen(
        playlistId = state.albumWithMusics.album.albumId,
        playlistWithMusics = state.allPlaylists,
        albumName = state.albumWithMusics.album.albumName,
        artistName = state.albumWithMusics.artist?.artistName ?: "",
        image = retrieveCoverMethod(state.albumWithMusics.album.coverId),
        musics = state.albumWithMusics.musics,
        navigateToModifyPlaylist = {
            navigateToModifyAlbum(selectedAlbumId)
        },
        navigateToArtist = navigateToArtist,
        navigateToModifyMusic = navigateToModifyMusic,
        navigateBack = navigateBack,
        retrieveCoverMethod = { retrieveCoverMethod(it) },
        updateNbPlayedAction = {
            selectedAlbumViewModel.onEvent(
                SelectedAlbumEvent.AddNbPlayed(
                    it
                )
            )
        },
        playlistType = PlaylistType.ALBUM,
        isDeleteMusicDialogShown = state.isDeleteMusicDialogShown,
        isBottomSheetShown = state.isMusicBottomSheetShown,
        isAddToPlaylistBottomSheetShown = state.isAddToPlaylistBottomSheetShown,
        onSetBottomSheetVisibility = { isShown ->
            selectedAlbumViewModel.onEvent(
                SelectedAlbumEvent.SetMusicBottomSheetVisibility(
                    isShown = isShown
                )
            )
        },
        onSetDeleteMusicDialogVisibility = { isShown ->
            selectedAlbumViewModel.onEvent(
                SelectedAlbumEvent.SetDeleteMusicDialogVisibility(
                    isShown = isShown
                )
            )
        },
        onSetAddToPlaylistBottomSheetVisibility = { isShown ->
            selectedAlbumViewModel.onEvent(
                SelectedAlbumEvent.SetAddToPlaylistBottomSheetVisibility(
                    isShown = isShown
                )
            )
        },
        onDeleteMusic = { music ->
            selectedAlbumViewModel.onEvent(
                SelectedAlbumEvent.DeleteMusic(
                    musicId = music.musicId
                )
            )
        },
        onToggleQuickAccessState = { music ->
            selectedAlbumViewModel.onEvent(
                SelectedAlbumEvent.ToggleQuickAccessState(
                    music = music
                )
            )
        },
        onAddMusicToSelectedPlaylists = { selectedPlaylistsIds, selectedMusic ->
            selectedAlbumViewModel.onEvent(
                SelectedAlbumEvent.AddMusicToPlaylists(
                    musicId = selectedMusic.musicId,
                    selectedPlaylistsIds = selectedPlaylistsIds
                )
            )
        }
    )
}