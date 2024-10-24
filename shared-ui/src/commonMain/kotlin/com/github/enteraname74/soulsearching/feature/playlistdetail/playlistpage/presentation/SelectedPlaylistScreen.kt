package com.github.enteraname74.soulsearching.feature.playlistdetail.playlistpage.presentation

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.ext.isPreviousScreenAPlaylistDetails
import com.github.enteraname74.soulsearching.ext.safePush
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.presentation.ModifyMusicScreen
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.presentation.ModifyPlaylistScreen
import com.github.enteraname74.soulsearching.feature.playlistdetail.composable.PlaylistDetailScreen
import com.github.enteraname74.soulsearching.feature.playlistdetail.composable.PlaylistScreen
import com.github.enteraname74.soulsearching.feature.playlistdetail.playlistpage.domain.SelectedPlaylistNavigationState
import com.github.enteraname74.soulsearching.feature.playlistdetail.playlistpage.domain.SelectedPlaylistState
import com.github.enteraname74.soulsearching.feature.playlistdetail.playlistpage.domain.SelectedPlaylistViewModel
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import java.util.*

/**
 * Represent the view of the selected album screen.
 */
data class SelectedPlaylistScreen(
    private val selectedPlaylistId: String
) : PlaylistDetailScreen(selectedPlaylistId) {
    private val playlistId: UUID = UUID.fromString(selectedPlaylistId)

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<SelectedPlaylistViewModel>()

        val navigator = LocalNavigator.currentOrThrow
        val colorThemeManager = injectElement<ColorThemeManager>()

        val bottomSheetState by screenModel.bottomSheetState.collectAsState()
        val dialogState by screenModel.dialogState.collectAsState()
        val navigationState by screenModel.navigationState.collectAsState()
        val addToPlaylistBottomSheet by screenModel.addToPlaylistBottomSheet.collectAsState()

        var isPlaylistFetched: Boolean by rememberSaveable {
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
                    navigator.safePush(ModifyMusicScreen(selectedMusicId = selectedMusic.musicId.toString()))
                    screenModel.consumeNavigation()
                }

                is SelectedPlaylistNavigationState.ToEdit -> {
                    navigator.safePush(ModifyPlaylistScreen(selectedPlaylistId = selectedPlaylistId))
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
                if (!navigator.isPreviousScreenAPlaylistDetails()) {
                    colorThemeManager.removePlaylistTheme()
                }
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
            onShowMusicBottomSheet = { selectedMusic ->
                selectedPlaylistViewModel.showMusicBottomSheet(
                    selectedMusic = selectedMusic,
                    currentPlaylist = (state as SelectedPlaylistState.Data).selectedPlaylist,
                )
            },
        )
    }
}