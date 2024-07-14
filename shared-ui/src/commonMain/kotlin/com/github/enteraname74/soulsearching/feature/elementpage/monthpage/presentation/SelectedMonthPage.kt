package com.github.enteraname74.soulsearching.feature.elementpage.monthpage.presentation

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
import com.github.enteraname74.soulsearching.feature.elementpage.monthpage.domain.SelectedMonthEvent
import com.github.enteraname74.soulsearching.feature.elementpage.monthpage.domain.SelectedMonthViewModel
import com.github.enteraname74.soulsearching.feature.elementpage.playlistpage.presentation.composable.PlaylistScreen
import com.github.enteraname74.soulsearching.feature.modifyelement.modifymusic.presentation.ModifyMusicScreen
import java.util.*

/**
 * Represent the view of the selected month screen.
 */
data class SelectedMonthScreen(
    private val month: String
): Screen {
    @Composable
    override fun Content() {
        val screeModel = getScreenModel<SelectedMonthViewModel>()
        val allImagesViewModel = getScreenModel<AllImageCoversViewModel>()

        val navigator = LocalNavigator.currentOrThrow
        val colorThemeManager = injectElement<ColorThemeManager>()

        SelectedMonthScreenView(
            selectedMonth = month,
            selectedMonthViewModel = screeModel,
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
fun SelectedMonthScreenView(
    selectedMonthViewModel: SelectedMonthViewModel,
    selectedMonth: String,
    navigateToModifyMusic: (String) -> Unit,
    navigateBack: () -> Unit,
    retrieveCoverMethod: (UUID?) -> ImageBitmap?,
) {
    var isMonthFetched by remember {
        mutableStateOf(false)
    }

    if (!isMonthFetched) {
        selectedMonthViewModel.onEvent(
            SelectedMonthEvent.SetSelectedMonth(
                month = selectedMonth
            )
        )
        isMonthFetched = true
    }

    val state by selectedMonthViewModel.state.collectAsState()

    PlaylistScreen(
        playlistId = null,
        playlistWithMusics = state.allPlaylists,
        playlistName = state.monthMusicList?.month ?: "",
        playlistCover = retrieveCoverMethod(state.monthMusicList?.coverId),
        musics = state.monthMusicList?.musics ?: emptyList(),
        navigateToModifyMusic = navigateToModifyMusic,
        navigateBack = navigateBack,
        retrieveCoverMethod = { retrieveCoverMethod(it) },
        updateNbPlayedAction = {
            selectedMonthViewModel.onEvent(
                SelectedMonthEvent.AddNbPlayed(
                    it
                )
            )
        },
        playlistType = PlaylistType.MONTH,
        isDeleteMusicDialogShown = state.isDeleteMusicDialogShown,
        isBottomSheetShown = state.isMusicBottomSheetShown,
        isAddToPlaylistBottomSheetShown = state.isAddToPlaylistBottomSheetShown,
        isRemoveFromPlaylistDialogShown = state.isRemoveFromPlaylistDialogShown,
        onSetBottomSheetVisibility = { isShown ->
            selectedMonthViewModel.onEvent(
                SelectedMonthEvent.SetMusicBottomSheetVisibility(
                    isShown = isShown
                )
            )
        },
        onSetDeleteMusicDialogVisibility = { isShown ->
            selectedMonthViewModel.onEvent(
                SelectedMonthEvent.SetDeleteMusicDialogVisibility(
                    isShown = isShown
                )
            )
        },
        onSetAddToPlaylistBottomSheetVisibility = { isShown ->
            selectedMonthViewModel.onEvent(
                SelectedMonthEvent.SetAddToPlaylistBottomSheetVisibility(
                    isShown = isShown
                )
            )
        },
        onDeleteMusic = { music ->
            selectedMonthViewModel.onEvent(
                SelectedMonthEvent.DeleteMusic(
                    musicId = music.musicId
                )
            )
        },
        onToggleQuickAccessState = { music ->
            selectedMonthViewModel.onEvent(
                SelectedMonthEvent.ToggleQuickAccessState(
                    music = music
                )
            )
        },
        onAddMusicToSelectedPlaylists = { selectedPlaylistsIds, selectedMusic ->
            selectedMonthViewModel.onEvent(
                SelectedMonthEvent.AddMusicToPlaylists(
                    musicId = selectedMusic.musicId,
                    selectedPlaylistsIds = selectedPlaylistsIds
                )
            )
        }
    )
}
