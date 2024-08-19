package com.github.enteraname74.soulsearching.feature.elementpage.playlistpage.presentation

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.coreui.theme.color.ColorThemeManager
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.feature.elementpage.composable.PlaylistScreen
import com.github.enteraname74.soulsearching.feature.elementpage.playlistpage.domain.SelectedPlaylistNavigationState
import com.github.enteraname74.soulsearching.feature.elementpage.playlistpage.domain.SelectedPlaylistState
import com.github.enteraname74.soulsearching.feature.elementpage.playlistpage.domain.SelectedPlaylistViewModel
import com.github.enteraname74.soulsearching.feature.modifyelement.modifymusic.presentation.ModifyMusicScreen
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyplaylist.presentation.ModifyPlaylistScreen
import java.util.*

/**
 * Represent the view of the selected album screen.
 */
data class SelectedPlaylistScreen(
    private val selectedPlaylistId: String
) : Screen {
    private val playlistId: UUID = UUID.fromString(selectedPlaylistId)

    @Composable
    override fun Content() {
        val screenModel = getScreenModel<SelectedPlaylistViewModel>()

        val navigator = LocalNavigator.currentOrThrow
        val colorThemeManager = injectElement<ColorThemeManager>()

        val bottomSheetState by screenModel.bottomSheetState.collectAsState()
        val dialogState by screenModel.dialogState.collectAsState()
        val navigationState by screenModel.navigationState.collectAsState()
        val addToPlaylistBottomSheet by screenModel.addToPlaylistBottomSheet.collectAsState()

        var isPlaylistFetched: Boolean by remember {
            mutableStateOf(false)
        }

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

                is SelectedPlaylistNavigationState.ToEdit -> {
                    navigator.push(ModifyPlaylistScreen(selectedPlaylistId = selectedPlaylistId))
                    screenModel.consumeNavigation()
                }
            }
        }

        LaunchedEffect(isPlaylistFetched) {
            if (!isPlaylistFetched) {
                screenModel.init(playlistId = playlistId)
                isPlaylistFetched = true
            }
        }

        SelectedPlaylistScreenView(
            selectedPlaylistViewModel = screenModel,
            navigateBack = {
                colorThemeManager.removePlaylistTheme()
                navigator.pop()
            },
        )
    }

}

@Composable
fun SelectedPlaylistScreenView(
    selectedPlaylistViewModel: SelectedPlaylistViewModel,
    navigateBack: () -> Unit,
) {

    val state by selectedPlaylistViewModel.state.collectAsState()

    when (state) {
        SelectedPlaylistState.Loading -> SoulLoadingScreen(
            navigateBack = navigateBack,
        )
        is SelectedPlaylistState.Data -> PlaylistScreen(
            playlistDetail = (state as SelectedPlaylistState.Data).playlistDetail,
            playlistDetailListener = selectedPlaylistViewModel,
            navigateBack = navigateBack,
            onShowMusicBottomSheet = selectedPlaylistViewModel::showMusicBottomSheet,
        )
    }
}