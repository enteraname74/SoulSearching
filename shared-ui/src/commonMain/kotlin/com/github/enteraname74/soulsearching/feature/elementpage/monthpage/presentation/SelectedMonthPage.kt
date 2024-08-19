package com.github.enteraname74.soulsearching.feature.elementpage.monthpage.presentation

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.coreui.theme.color.ColorThemeManager
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.feature.elementpage.composable.PlaylistScreen
import com.github.enteraname74.soulsearching.feature.elementpage.monthpage.domain.SelectedMonthNavigationState
import com.github.enteraname74.soulsearching.feature.elementpage.monthpage.domain.SelectedMonthState
import com.github.enteraname74.soulsearching.feature.elementpage.monthpage.domain.SelectedMonthViewModel
import com.github.enteraname74.soulsearching.feature.modifyelement.modifymusic.presentation.ModifyMusicScreen

/**
 * Represent the view of the selected month screen.
 */
data class SelectedMonthScreen(
    private val month: String
): Screen {
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

        var isMonthFetched by remember {
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
