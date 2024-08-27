package com.github.enteraname74.soulsearching.feature.playlistdetail.monthpage.presentation

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.presentation.ModifyMusicScreen
import com.github.enteraname74.soulsearching.feature.playlistdetail.composable.PlaylistDetailScreen
import com.github.enteraname74.soulsearching.feature.playlistdetail.composable.PlaylistScreen
import com.github.enteraname74.soulsearching.feature.playlistdetail.monthpage.domain.SelectedMonthNavigationState
import com.github.enteraname74.soulsearching.feature.playlistdetail.monthpage.domain.SelectedMonthState
import com.github.enteraname74.soulsearching.feature.playlistdetail.monthpage.domain.SelectedMonthViewModel
import com.github.enteraname74.soulsearching.theme.ColorThemeManager

/**
 * Represent the view of the selected month screen.
 */
data class SelectedMonthScreen(
    private val month: String
): PlaylistDetailScreen(month) {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<SelectedMonthViewModel>()

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

        var isMonthFetched by rememberSaveable {
            mutableStateOf(false)
        }

        if (!isMonthFetched) {
            screenModel.init(month = month)
            isMonthFetched = true
        }

        SelectedMonthScreenView(
            selectedMonthViewModel = screenModel,
            navigateBack = {
                colorThemeManager.removePlaylistTheme()
                navigator.pop()
            },
        )
    }
}

@Composable
fun SelectedMonthScreenView(
    selectedMonthViewModel: SelectedMonthViewModel,
    navigateBack: () -> Unit,
) {
    val state by selectedMonthViewModel.state.collectAsState()

    when (state) {
        SelectedMonthState.Loading -> SoulLoadingScreen(
            navigateBack = navigateBack,
        )
        is SelectedMonthState.Data -> PlaylistScreen(
            playlistDetail = (state as SelectedMonthState.Data).playlistDetail,
            playlistDetailListener = selectedMonthViewModel,
            navigateBack = navigateBack,
            onShowMusicBottomSheet = selectedMonthViewModel::showMusicBottomSheet,
        )
    }
}
