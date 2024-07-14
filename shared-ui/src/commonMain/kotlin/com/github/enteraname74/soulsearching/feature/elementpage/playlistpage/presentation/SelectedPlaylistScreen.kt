package com.github.enteraname74.soulsearching.feature.elementpage.playlistpage.presentation

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.theme.color.ColorThemeManager
import com.github.enteraname74.soulsearching.domain.di.injectElement
import com.github.enteraname74.soulsearching.feature.coversprovider.AllImageCoversViewModel
import com.github.enteraname74.soulsearching.feature.elementpage.domain.PlaylistType
import com.github.enteraname74.soulsearching.feature.elementpage.playlistpage.domain.SelectedPlaylistEvent
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
        )
    }

}

@Composable
fun SelectedPlaylistScreenView(
    selectedPlaylistViewModel: SelectedPlaylistViewModel,
    navigateToModifyPlaylist: (String) -> Unit,
    selectedPlaylistId: String,
    navigateToModifyMusic: (String) -> Unit,
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
        playlistWithMusics = state.allPlaylists,
        playlistName = state.playlistWithMusics?.playlist?.name ?: "",
        playlistCover = retrieveCoverMethod(state.playlistWithMusics?.playlist?.coverId),
        musics = state.playlistWithMusics?.musics ?: emptyList(),
        navigateToModifyPlaylist = {
            navigateToModifyPlaylist(selectedPlaylistId)
        },
        navigateToModifyMusic = navigateToModifyMusic,
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
        isDeleteMusicDialogShown = state.isDeleteMusicDialogShown,
        isBottomSheetShown = state.isMusicBottomSheetShown,
        isAddToPlaylistBottomSheetShown = state.isAddToPlaylistBottomSheetShown,
        isRemoveFromPlaylistDialogShown = state.isRemoveFromPlaylistDialogShown,
        onSetBottomSheetVisibility = { isShown ->
            selectedPlaylistViewModel.onEvent(
                SelectedPlaylistEvent.SetMusicBottomSheetVisibility(
                    isShown = isShown
                )
            )
        },
        onSetDeleteMusicDialogVisibility = { isShown ->
            selectedPlaylistViewModel.onEvent(
                SelectedPlaylistEvent.SetDeleteMusicDialogVisibility(
                    isShown = isShown
                )
            )
        },
        onSetRemoveMusicFromPlaylistDialogVisibility = { isShown ->
            selectedPlaylistViewModel.onEvent(
                SelectedPlaylistEvent.SetRemoveFromPlaylistDialogVisibility(
                    isShown = isShown
                )
            )
        },
        onSetAddToPlaylistBottomSheetVisibility = { isShown ->
            selectedPlaylistViewModel.onEvent(
                SelectedPlaylistEvent.SetAddToPlaylistBottomSheetVisibility(
                    isShown = isShown
                )
            )
        },
        onDeleteMusic = { music ->
            selectedPlaylistViewModel.onEvent(
                SelectedPlaylistEvent.DeleteMusic(
                    musicId = music.musicId
                )
            )
        },
        onToggleQuickAccessState = { music ->
            selectedPlaylistViewModel.onEvent(
                SelectedPlaylistEvent.ToggleQuickAccessState(
                    music = music
                )
            )
        },
        onRemoveFromPlaylist = { music ->
            state.playlistWithMusics?.playlist?.playlistId?.let { playlistId ->
                selectedPlaylistViewModel.onEvent(
                    SelectedPlaylistEvent.RemoveMusicFromPlaylist(
                        musicId = music.musicId,
                        playlistId = playlistId
                    )
                )
            }
        },
        onAddMusicToSelectedPlaylists = { selectedPlaylistsIds, selectedMusic ->
            selectedPlaylistViewModel.onEvent(
                SelectedPlaylistEvent.AddMusicToPlaylists(
                    musicId = selectedMusic.musicId,
                    selectedPlaylistsIds = selectedPlaylistsIds
                )
            )
        }
    )
}