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
        val addToPlaylistBottomSheet by screenModel.addToPlaylistBottomSheet.collectAsState()

        bottomSheetState?.BottomSheet()
        dialogState?.Dialog()
        addToPlaylistBottomSheet?.BottomSheet()

        LaunchedEffect(navigationState) {
            when(navigationState) {
                SelectedArtistNavigationState.Idle -> { /*no-op*/  }
                is SelectedArtistNavigationState.ToModifyAlbum -> {
                    val selectedAlbum = (navigationState as SelectedArtistNavigationState.ToModifyAlbum).album
                    navigator.push(ModifyAlbumScreen(selectedAlbumId = selectedAlbum.albumId.toString()))
                    screenModel.consumeNavigation()
                }

                is SelectedArtistNavigationState.ToModifyMusic -> {
                    val selectedMusic = (navigationState as SelectedArtistNavigationState.ToModifyMusic).music
                    navigator.push(ModifyMusicScreen(selectedMusicId = selectedMusic.musicId.toString()))
                    screenModel.consumeNavigation()
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
        )
    }
}

@Composable
fun SelectedArtistScreenView(
    selectedArtistViewModel: SelectedArtistViewModel,
    selectedArtistId: String,
    navigateToModifyArtist: (String) -> Unit,
    navigateToAlbum: (String) -> Unit,
    navigateBack: () -> Unit,
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
        showMusicBottomSheet = selectedArtistViewModel::showMusicBottomSheet,
        albums = state.artistAlbums,
        navigateToAlbum = navigateToAlbum,
        showAlbumBottomSheet = selectedArtistViewModel::showAlbumBottomSheet,
    )
}