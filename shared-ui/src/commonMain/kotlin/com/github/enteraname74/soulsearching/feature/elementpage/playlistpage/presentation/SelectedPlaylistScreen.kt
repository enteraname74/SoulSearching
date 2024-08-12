package com.github.enteraname74.soulsearching.feature.elementpage.playlistpage.presentation

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.theme.color.ColorThemeManager
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.feature.coversprovider.AllImageCoversViewModel
import com.github.enteraname74.soulsearching.feature.elementpage.domain.PlaylistType
import com.github.enteraname74.soulsearching.feature.elementpage.playlistpage.domain.SelectedPlaylistEvent
import com.github.enteraname74.soulsearching.feature.elementpage.playlistpage.domain.SelectedPlaylistNavigationState
import com.github.enteraname74.soulsearching.feature.elementpage.playlistpage.domain.SelectedPlaylistViewModel
import com.github.enteraname74.soulsearching.feature.elementpage.playlistpage.presentation.composable.PlaylistScreen
import com.github.enteraname74.soulsearching.feature.modifyelement.modifymusic.presentation.ModifyMusicScreen
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyplaylist.presentation.ModifyPlaylistScreen
import java.util.*

/**
 * Represent the view of the selected album screen.
 */
data class SelectedPlaylistScreen(
    private val selectedPlaylistId: String
) : Screen {

    @Composable
    override fun Content() {
        val screenModel = getScreenModel<SelectedPlaylistViewModel>()
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
                SelectedPlaylistNavigationState.Idle -> { /*no-op*/  }
                is SelectedPlaylistNavigationState.ToModifyMusic -> {
                    val selectedMusic = (navigationState as SelectedPlaylistNavigationState.ToModifyMusic).music
                    navigator.push(ModifyMusicScreen(selectedMusicId = selectedMusic.musicId.toString()))
                    screenModel.consumeNavigation()
                }
            }
        }

        SelectedPlaylistScreenView(
            selectedPlaylistViewModel = screenModel,
            navigateToModifyPlaylist = {
                navigator.push(
                    ModifyPlaylistScreen(
                        selectedPlaylistId = selectedPlaylistId
                    )
                )
            },
            selectedPlaylistId = selectedPlaylistId,
            navigateBack = {
                colorThemeManager.removePlaylistTheme()
                navigator.pop()
            },
            retrieveCoverMethod = allImagesViewModel::getImageCover,
        )
    }

}

@Composable
fun SelectedPlaylistScreenView(
    selectedPlaylistViewModel: SelectedPlaylistViewModel,
    navigateToModifyPlaylist: (String) -> Unit,
    selectedPlaylistId: String,
    navigateBack: () -> Unit,
    retrieveCoverMethod: (UUID?) -> ImageBitmap?,
) {
    var isPlaylistFetched by remember {
        mutableStateOf(false)
    }

    if (!isPlaylistFetched) {
        selectedPlaylistViewModel.onEvent(
            SelectedPlaylistEvent.SetSelectedPlaylist(
                playlistId = UUID.fromString(selectedPlaylistId)
            )
        )
        isPlaylistFetched = true
    }

    val state by selectedPlaylistViewModel.state.collectAsState()

    PlaylistScreen(
        playlistId = state.playlistWithMusics?.playlist?.playlistId,
        playlistName = state.playlistWithMusics?.playlist?.name ?: "",
        playlistCover = retrieveCoverMethod(state.playlistWithMusics?.playlist?.coverId),
        musics = state.playlistWithMusics?.musics ?: emptyList(),
        navigateToModifyPlaylist = {
            navigateToModifyPlaylist(selectedPlaylistId)
        },
        navigateBack = navigateBack,
        retrieveCoverMethod = { retrieveCoverMethod(it) },
        updateNbPlayedAction = {
            selectedPlaylistViewModel.onEvent(
                SelectedPlaylistEvent.AddNbPlayed(
                    it
                )
            )
        },
        playlistType = PlaylistType.PLAYLIST,
        onShowMusicBottomSheet = { selectedMusic ->
            selectedPlaylistViewModel.showMusicBottomSheet(
                selectedMusic = selectedMusic,
                currentPlaylist = state.playlistWithMusics?.playlist,
            )
        }
    )
}