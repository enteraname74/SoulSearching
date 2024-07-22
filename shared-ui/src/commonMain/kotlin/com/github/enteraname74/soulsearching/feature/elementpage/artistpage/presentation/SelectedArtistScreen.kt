package com.github.enteraname74.soulsearching.feature.elementpage.artistpage.presentation

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.theme.color.ColorThemeManager
import com.github.enteraname74.soulsearching.domain.di.injectElement
import com.github.enteraname74.soulsearching.feature.coversprovider.AllImageCoversViewModel
import com.github.enteraname74.soulsearching.feature.elementpage.albumpage.presentation.SelectedAlbumScreen
import com.github.enteraname74.soulsearching.feature.elementpage.artistpage.domain.SelectedArtistEvent
import com.github.enteraname74.soulsearching.feature.elementpage.artistpage.domain.SelectedArtistNavigationState
import com.github.enteraname74.soulsearching.feature.elementpage.artistpage.domain.SelectedArtistViewModel
import com.github.enteraname74.soulsearching.feature.elementpage.artistpage.presentation.composable.ArtistScreen
import com.github.enteraname74.soulsearching.feature.elementpage.domain.PlaylistType
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyalbum.presentation.ModifyAlbumScreen
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyartist.presentation.ModifyArtistScreen
import com.github.enteraname74.soulsearching.feature.modifyelement.modifymusic.presentation.ModifyMusicScreen
import java.util.*

/**
 * Represent the view of the selected artist screen.
 */
data class SelectedArtistScreen(
    private val selectedArtistId: String
) : Screen {

    @Composable
    override fun Content() {
        val screenModel = getScreenModel<SelectedArtistViewModel>()
        val allImagesViewModel = getScreenModel<AllImageCoversViewModel>()

        val navigator = LocalNavigator.currentOrThrow
        val colorThemeManager = injectElement<ColorThemeManager>()

        val bottomSheetState by screenModel.bottomSheetState.collectAsState()
        val dialogState by screenModel.dialogState.collectAsState()
        val navigationState by screenModel.navigationState.collectAsState()

        bottomSheetState?.BottomSheet()
        dialogState?.Dialog()

        LaunchedEffect(navigationState) {
            when(navigationState) {
                SelectedArtistNavigationState.Idle -> { /*no-op*/  }
                is SelectedArtistNavigationState.ToModifyAlbum -> {
                    val selectedAlbum = (navigationState as SelectedArtistNavigationState.ToModifyAlbum).album

                    navigator.push(ModifyAlbumScreen(selectedAlbumId = selectedAlbum.albumId.toString()))
                }
            }
        }

        SelectedArtistScreenView(
            selectedArtistViewModel = screenModel,
            selectedArtistId = selectedArtistId,
            navigateToModifyArtist = {
                navigator.push(
                    ModifyArtistScreen(
                        selectedArtistId = selectedArtistId
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
            navigateBack = {
                colorThemeManager.removePlaylistTheme()
                navigator.pop()
            },
            retrieveCoverMethod = allImagesViewModel::getImageCover,
            navigateToAlbum = { albumId ->
                navigator.push(
                    SelectedAlbumScreen(
                        selectedAlbumId = albumId
                    )
                )
            },
            navigateToModifyAlbum = { albumId ->
                navigator.push(
                    ModifyAlbumScreen(
                        selectedAlbumId = albumId
                    )
                )
            }
        )
    }
}

@Composable
@Suppress("Deprecation")
fun SelectedArtistScreenView(
    selectedArtistViewModel: SelectedArtistViewModel,
    selectedArtistId: String,
    navigateToModifyArtist: (String) -> Unit,
    navigateToModifyMusic: (String) -> Unit,
    navigateToAlbum: (String) -> Unit,
    navigateBack: () -> Unit,
    navigateToModifyAlbum: (String) -> Unit,
    retrieveCoverMethod: (UUID?) -> ImageBitmap?,
) {
    var isArtistFetched by remember {
        mutableStateOf(false)
    }

    if (!isArtistFetched) {
        selectedArtistViewModel.onEvent(
            SelectedArtistEvent.SetSelectedArtist(
                artistId = UUID.fromString(selectedArtistId)
            )
        )
        isArtistFetched = true
    }

    val state by selectedArtistViewModel.state.collectAsState()

//    if (musicState.musics.isEmpty()) {
//        SideEffect {
//            CoroutineScope(Dispatchers.IO).launch {
//                if (!selectedArtistViewModel.doesArtistExists(UUID.fromString(selectedArtistId))) {
//                    withContext(Dispatchers.Main) {
//                        navigateBack()
//                    }
//                }
//            }
//        }
//    }

    ArtistScreen(
        playlistId = state.artistWithMusics.artist.artistId,
        playlistWithMusics = state.allPlaylists,
        artistName = state.artistWithMusics.artist.artistName,
        artistCover = retrieveCoverMethod(state.artistWithMusics.artist.coverId),
        musics = state.artistWithMusics.musics,
        navigateToModifyArtist = {
            navigateToModifyArtist(selectedArtistId)
        },
        navigateBack = navigateBack,
        retrieveCoverMethod = { retrieveCoverMethod(it) },
        updateNbPlayedAction = {
            selectedArtistViewModel.onEvent(
                SelectedArtistEvent.AddNbPlayed(
                    it
                )
            )
        },
        playlistType = PlaylistType.ARTIST,
        isAddToPlaylistBottomSheetShown = state.isAddToPlaylistBottomSheetShown,
        showMusicBottomSheet = { isShown ->
            selectedArtistViewModel.onEvent(
                SelectedArtistEvent.SetMusicBottomSheetVisibility(
                    isShown = isShown
                )
            )
        },
        onSetDeleteMusicDialogVisibility = { isShown ->
            selectedArtistViewModel.onEvent(
                SelectedArtistEvent.SetDeleteMusicDialogVisibility(
                    isShown = isShown
                )
            )
        },
        onSetAddToPlaylistBottomSheetVisibility = { isShown ->
            selectedArtistViewModel.onEvent(
                SelectedArtistEvent.SetAddToPlaylistBottomSheetVisibility(
                    isShown = isShown
                )
            )
        },
        onDeleteMusic = { music ->
            selectedArtistViewModel.onEvent(
                SelectedArtistEvent.DeleteMusic(
                    musicId = music.musicId
                )
            )
        },
        onToggleQuickAccessState = { music ->
            selectedArtistViewModel.onEvent(
                SelectedArtistEvent.ToggleQuickAccessState(
                    music = music
                )
            )
        },
        onAddMusicToSelectedPlaylists = { selectedPlaylistsIds, selectedMusic ->
            selectedArtistViewModel.onEvent(
                SelectedArtistEvent.AddMusicToPlaylists(
                    musicId = selectedMusic.musicId,
                    selectedPlaylistsIds = selectedPlaylistsIds
                )
            )
        },
        albums = state.artistAlbums,
        navigateToAlbum = navigateToAlbum,
        isDeleteAlbumDialogShown = state.isDeleteAlbumDialogShown,
        isAlbumBottomSheetShown = state.isAlbumBottomSheetShown,
        navigateToModifyAlbum = navigateToModifyAlbum,
        showAlbumBottomSheet = { isShown ->
            selectedArtistViewModel.onEvent(
                SelectedArtistEvent.SetAlbumBottomSheetVisibility(
                    isShown = isShown
                )
            )
        },
        onSetDeleteAlbumDialogVisibility = { isShown ->
            selectedArtistViewModel.onEvent(
                SelectedArtistEvent.SetDeleteAlbumDialogVisibility(
                    isShown = isShown
                )
            )
        },
        onToggleAlbumQuickAccessState = { album ->
            selectedArtistViewModel.onEvent(
                SelectedArtistEvent.ToggleAlbumQuickAccessState(
                    album = album
                )
            )
        },
        onDeleteAlbum = { album ->
            selectedArtistViewModel.onEvent(
                SelectedArtistEvent.DeleteAlbum(albumId = album.albumId)
            )
        }
    )
}