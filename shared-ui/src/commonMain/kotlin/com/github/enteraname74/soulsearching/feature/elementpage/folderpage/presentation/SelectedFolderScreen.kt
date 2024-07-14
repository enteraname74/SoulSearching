package com.github.enteraname74.soulsearching.feature.elementpage.folderpage.presentation

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
import com.github.enteraname74.soulsearching.feature.elementpage.folderpage.domain.SelectedFolderEvent
import com.github.enteraname74.soulsearching.feature.elementpage.folderpage.domain.SelectedFolderViewModel
import com.github.enteraname74.soulsearching.feature.elementpage.playlistpage.presentation.composable.PlaylistScreen
import com.github.enteraname74.soulsearching.feature.modifyelement.modifymusic.presentation.ModifyMusicScreen
import java.util.*

/**
 * Represent the view of the selected folder screen.
 */
data class SelectedFolderScreen(
    private val folderPath: String
): Screen {
    @Composable
    override fun Content() {
        val screeModel = getScreenModel<SelectedFolderViewModel>()
        val allImagesViewModel = getScreenModel<AllImageCoversViewModel>()

        val navigator = LocalNavigator.currentOrThrow
        val colorThemeManager = injectElement<ColorThemeManager>()

        SelectedFolderScreenView(
            selectedFolderPath = folderPath,
            selectedFolderViewModel = screeModel,
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
fun SelectedFolderScreenView(
    selectedFolderViewModel: SelectedFolderViewModel,
    selectedFolderPath: String,
    navigateToModifyMusic: (String) -> Unit,
    navigateBack: () -> Unit,
    retrieveCoverMethod: (UUID?) -> ImageBitmap?,
) {
    var isFolderFetched by remember {
        mutableStateOf(false)
    }

    if (!isFolderFetched) {
        selectedFolderViewModel.onEvent(
            SelectedFolderEvent.SetSelectedFolder(
                folderPath = selectedFolderPath
            )
        )
        isFolderFetched = true
    }

    val state by selectedFolderViewModel.state.collectAsState()

    PlaylistScreen(
        playlistId = null,
        playlistWithMusics = state.allPlaylists,
        playlistName = state.musicFolder?.path ?: "",
        playlistCover = retrieveCoverMethod(state.musicFolder?.coverId),
        musics = state.musicFolder?.musics ?: emptyList(),
        navigateToModifyMusic = navigateToModifyMusic,
        navigateBack = navigateBack,
        retrieveCoverMethod = { retrieveCoverMethod(it) },
        updateNbPlayedAction = {
            selectedFolderViewModel.onEvent(
                SelectedFolderEvent.AddNbPlayed(
                    it
                )
            )
        },
        playlistType = PlaylistType.FOLDER,
        isDeleteMusicDialogShown = state.isDeleteMusicDialogShown,
        isBottomSheetShown = state.isMusicBottomSheetShown,
        isAddToPlaylistBottomSheetShown = state.isAddToPlaylistBottomSheetShown,
        isRemoveFromPlaylistDialogShown = state.isRemoveFromPlaylistDialogShown,
        onSetBottomSheetVisibility = { isShown ->
            selectedFolderViewModel.onEvent(
                SelectedFolderEvent.SetMusicBottomSheetVisibility(
                    isShown = isShown
                )
            )
        },
        onSetDeleteMusicDialogVisibility = { isShown ->
            selectedFolderViewModel.onEvent(
                SelectedFolderEvent.SetDeleteMusicDialogVisibility(
                    isShown = isShown
                )
            )
        },
        onSetAddToPlaylistBottomSheetVisibility = { isShown ->
            selectedFolderViewModel.onEvent(
                SelectedFolderEvent.SetAddToPlaylistBottomSheetVisibility(
                    isShown = isShown
                )
            )
        },
        onDeleteMusic = { music ->
            selectedFolderViewModel.onEvent(
                SelectedFolderEvent.DeleteMusic(
                    musicId = music.musicId
                )
            )
        },
        onToggleQuickAccessState = { music ->
            selectedFolderViewModel.onEvent(
                SelectedFolderEvent.ToggleQuickAccessState(
                    music = music
                )
            )
        },
        onAddMusicToSelectedPlaylists = { selectedPlaylistsIds, selectedMusic ->
            selectedFolderViewModel.onEvent(
                SelectedFolderEvent.AddMusicToPlaylists(
                    musicId = selectedMusic.musicId,
                    selectedPlaylistsIds = selectedPlaylistsIds
                )
            )
        }
    )
}
