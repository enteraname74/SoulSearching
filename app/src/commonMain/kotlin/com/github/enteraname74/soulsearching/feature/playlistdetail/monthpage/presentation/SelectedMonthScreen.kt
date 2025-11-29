package com.github.enteraname74.soulsearching.feature.playlistdetail.monthpage.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.enteraname74.soulsearching.coreui.multiselection.SelectionMode
import com.github.enteraname74.soulsearching.coreui.screen.SoulErrorScreen
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.feature.playlistdetail.composable.PlaylistScreen
import com.github.enteraname74.soulsearching.feature.playlistdetail.monthpage.domain.SelectedMonthNavigationState
import com.github.enteraname74.soulsearching.feature.playlistdetail.monthpage.domain.SelectedMonthState
import com.github.enteraname74.soulsearching.feature.playlistdetail.monthpage.domain.SelectedMonthViewModel

@Composable
fun SelectedMonthRoute(
    viewModel: SelectedMonthViewModel,
    onNavigationState: (SelectedMonthNavigationState) -> Unit,
) {
    val bottomSheetState by viewModel.bottomSheetState.collectAsState()
    val dialogState by viewModel.dialogState.collectAsState()
    val navigationState by viewModel.navigationState.collectAsState()
    val addToPlaylistBottomSheet by viewModel.addToPlaylistBottomSheet.collectAsState()

    bottomSheetState?.BottomSheet()
    dialogState?.Dialog()
    addToPlaylistBottomSheet?.BottomSheet()

    LaunchedEffect(navigationState) {
        onNavigationState(navigationState)
        viewModel.consumeNavigation()
    }

    SelectedMonthScreenView(
        selectedMonthViewModel = viewModel,
        navigateBack = viewModel::navigateBack,
    )
}

@Composable
fun SelectedMonthScreenView(
    selectedMonthViewModel: SelectedMonthViewModel,
    navigateBack: () -> Unit,
) {
    val state by selectedMonthViewModel.state.collectAsState()
    val multiSelectionState by selectedMonthViewModel.multiSelectionState.collectAsState()

    when (state) {
        SelectedMonthState.Loading -> SoulLoadingScreen(
            navigateBack = navigateBack,
        )
        is SelectedMonthState.Data -> PlaylistScreen(
            playlistDetail = (state as SelectedMonthState.Data).playlistDetail,
            playlistDetailListener = selectedMonthViewModel,
            navigateBack = navigateBack,
            onShowMusicBottomSheet = selectedMonthViewModel::showMusicBottomSheet,
            multiSelectionManagerImpl = selectedMonthViewModel.multiSelectionManagerImpl,
            onLongSelectOnMusic = {
                selectedMonthViewModel.toggleElementInSelection(
                    id = it.musicId,
                    mode = SelectionMode.Music,
                )
            },
            multiSelectionState = multiSelectionState,
        )

        SelectedMonthState.Error -> {
            SoulErrorScreen(
                leftAction = TopBarNavigationAction(navigateBack),
                text = strings.monthPlaylistDoesNotExists,
            )
        }
    }
}
