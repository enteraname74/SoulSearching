package com.github.enteraname74.soulsearching.feature.elementpage.folderpage.presentation

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.coreui.theme.color.ColorThemeManager
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.feature.elementpage.composable.PlaylistScreen
import com.github.enteraname74.soulsearching.feature.elementpage.folderpage.domain.SelectedFolderNavigationState
import com.github.enteraname74.soulsearching.feature.elementpage.folderpage.domain.SelectedFolderState
import com.github.enteraname74.soulsearching.feature.elementpage.folderpage.domain.SelectedFolderViewModel
import com.github.enteraname74.soulsearching.feature.modifyelement.modifymusic.presentation.ModifyMusicScreen

/**
 * Represent the view of the selected folder screen.
 */
data class SelectedFolderScreen(
    private val folderPath: String
): Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<SelectedFolderViewModel>()
        val state by screenModel.state.collectAsState()
        println("FROM STATE: $state")

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

        var isFolderFetched by remember {
            mutableStateOf(false)
        }

        println("FETCHED? $isFolderFetched")

        if (!isFolderFetched) {
            println("Will init")
            screenModel.init(folderPath = folderPath)
            isFolderFetched = true
        }

        SelectedFolderScreenView(
            selectedFolderViewModel = screenModel,
            navigateBack = {
                colorThemeManager.removePlaylistTheme()
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
