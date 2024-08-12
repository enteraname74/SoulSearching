package com.github.enteraname74.soulsearching.feature.elementpage.folderpage.presentation

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
import com.github.enteraname74.soulsearching.feature.elementpage.folderpage.domain.SelectedFolderEvent
import com.github.enteraname74.soulsearching.feature.elementpage.folderpage.domain.SelectedFolderNavigationState
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
        val screenModel = getScreenModel<SelectedFolderViewModel>()
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
                SelectedFolderNavigationState.Idle -> { /*no-op*/  }
                is SelectedFolderNavigationState.ToModifyMusic -> {
                    val selectedMusic = (navigationState as SelectedFolderNavigationState.ToModifyMusic).music
                    navigator.push(ModifyMusicScreen(selectedMusicId = selectedMusic.musicId.toString()))
                    screenModel.consumeNavigation()
                }
            }
        }

        SelectedFolderScreenView(
            selectedFolderPath = folderPath,
            selectedFolderViewModel = screenModel,
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
        playlistName = state.musicFolder?.path ?: "",
        playlistCover = retrieveCoverMethod(state.musicFolder?.coverId),
        musics = state.musicFolder?.musics ?: emptyList(),
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
        onShowMusicBottomSheet = selectedFolderViewModel::showMusicBottomSheet,
    )
}
