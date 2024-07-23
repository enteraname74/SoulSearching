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
import com.github.enteraname74.soulsearching.feature.elementpage.monthpage.domain.SelectedMonthNavigationState
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
        val screenModel = getScreenModel<SelectedMonthViewModel>()
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
                SelectedMonthNavigationState.Idle -> { /*no-op*/  }
                is SelectedMonthNavigationState.ToModifyMusic -> {
                    val selectedMusic = (navigationState as SelectedMonthNavigationState.ToModifyMusic).music
                    navigator.push(ModifyMusicScreen(selectedMusicId = selectedMusic.musicId.toString()))
                    screenModel.consumeNavigation()
                }
            }
        }

        SelectedMonthScreenView(
            selectedMonth = month,
            selectedMonthViewModel = screenModel,
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
        playlistName = state.monthMusicList?.month ?: "",
        playlistCover = retrieveCoverMethod(state.monthMusicList?.coverId),
        musics = state.monthMusicList?.musics ?: emptyList(),
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
        onShowMusicBottomSheet = selectedMonthViewModel::showMusicBottomSheet
    )
}
