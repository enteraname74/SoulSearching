package com.github.enteraname74.soulsearching.feature.playlistdetail.folderpage.presentation

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.ext.isPreviousScreenAPlaylistDetails
import com.github.enteraname74.soulsearching.ext.safePush
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.presentation.ModifyMusicScreen
import com.github.enteraname74.soulsearching.feature.playlistdetail.composable.PlaylistDetailScreen
import com.github.enteraname74.soulsearching.feature.playlistdetail.composable.PlaylistScreen
import com.github.enteraname74.soulsearching.feature.playlistdetail.folderpage.domain.SelectedFolderNavigationState
import com.github.enteraname74.soulsearching.feature.playlistdetail.folderpage.domain.SelectedFolderState
import com.github.enteraname74.soulsearching.feature.playlistdetail.folderpage.domain.SelectedFolderViewModel
import com.github.enteraname74.soulsearching.theme.ColorThemeManager

/**
 * Represent the view of the selected folder screen.
 */
data class SelectedFolderScreen(
    private val folderPath: String
): PlaylistDetailScreen(folderPath) {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<SelectedFolderViewModel>()

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
                    navigator.safePush(ModifyMusicScreen(selectedMusicId = selectedMusic.musicId.toString()))
                    screenModel.consumeNavigation()
                }
            }
        }

        var isFolderFetched by rememberSaveable {
            mutableStateOf(false)
        }

        if (!isFolderFetched) {
            screenModel.init(folderPath = folderPath)
            isFolderFetched = true
        }

        SelectedFolderScreenView(
            selectedFolderViewModel = screenModel,
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
fun SelectedFolderScreenView(
    selectedFolderViewModel: SelectedFolderViewModel,
    navigateBack: () -> Unit,
) {
    val state by selectedFolderViewModel.state.collectAsState()

    when (state) {
        is SelectedFolderState.Data -> PlaylistScreen(
            playlistDetail = (state as SelectedFolderState.Data).playlistDetail,
            playlistDetailListener = selectedFolderViewModel,
            navigateBack = navigateBack,
            onShowMusicBottomSheet = selectedFolderViewModel::showMusicBottomSheet,
        )
        SelectedFolderState.Loading -> SoulLoadingScreen(
            navigateBack = navigateBack,
        )
    }
}
