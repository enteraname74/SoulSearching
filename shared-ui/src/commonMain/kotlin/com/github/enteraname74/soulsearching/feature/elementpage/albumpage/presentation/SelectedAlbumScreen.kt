package com.github.enteraname74.soulsearching.feature.elementpage.albumpage.presentation

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.feature.elementpage.albumpage.domain.SelectedAlbumNavigationState
import com.github.enteraname74.soulsearching.feature.elementpage.albumpage.domain.SelectedAlbumState
import com.github.enteraname74.soulsearching.feature.elementpage.albumpage.domain.SelectedAlbumViewModel
import com.github.enteraname74.soulsearching.feature.elementpage.artistpage.presentation.SelectedArtistScreen
import com.github.enteraname74.soulsearching.feature.elementpage.composable.PlaylistScreen
import com.github.enteraname74.soulsearching.feature.elementpage.domain.PlaylistDetailScreen
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyalbum.presentation.ModifyAlbumScreen
import com.github.enteraname74.soulsearching.feature.modifyelement.modifymusic.presentation.ModifyMusicScreen
import java.util.*

/**
 * Represent the view of the selected album screen.
 */
data class SelectedAlbumScreen(
    private val selectedAlbumId: String,
) : Screen, PlaylistDetailScreen {
    private val albumId: UUID = UUID.fromString(selectedAlbumId)

    @Composable
    override fun Content() {
        val screenModel = getScreenModel<SelectedAlbumViewModel>()

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
                SelectedAlbumNavigationState.Idle -> { /*no-op*/ }
                is SelectedAlbumNavigationState.ToModifyMusic -> {
                    val selectedMusic = (navigationState as SelectedAlbumNavigationState.ToModifyMusic).music
                    navigator.push(ModifyMusicScreen(selectedMusicId = selectedMusic.musicId.toString()))
                    screenModel.consumeNavigation()
                }
                SelectedAlbumNavigationState.ToEdit -> {
                    navigator.push(ModifyAlbumScreen(selectedAlbumId = selectedAlbumId))
                    screenModel.consumeNavigation()
                }

                is SelectedAlbumNavigationState.ToArtist -> {
                    val selectedArtistId = (navigationState as SelectedAlbumNavigationState.ToArtist).artistId
                    navigator.push(SelectedArtistScreen(selectedArtistId = selectedArtistId.toString()))
                    screenModel.consumeNavigation()
                }
            }
        }

        var isAlbumFetched by rememberSaveable {
            mutableStateOf(false)
        }

        LaunchedEffect(isAlbumFetched) {
            if (!isAlbumFetched) {
                screenModel.init(albumId = albumId)
                isAlbumFetched = true
            }
        }

        SelectedAlbumScreenView(
            selectedAlbumViewModel = screenModel,
            navigateBack = {
                colorThemeManager.removePlaylistTheme()
                navigator.pop()
            },
        )
    }
}

@Composable
fun SelectedAlbumScreenView(
    selectedAlbumViewModel: SelectedAlbumViewModel,
    navigateBack: () -> Unit,
) {
    val state by selectedAlbumViewModel.state.collectAsState()

    when (state) {
        is SelectedAlbumState.Data -> PlaylistScreen(
            playlistDetail = (state as SelectedAlbumState.Data).playlistDetail,
            playlistDetailListener = selectedAlbumViewModel,
            navigateBack = navigateBack,
            onShowMusicBottomSheet = selectedAlbumViewModel::showMusicBottomSheet,
        )
        SelectedAlbumState.Loading -> SoulLoadingScreen(
            navigateBack = navigateBack,
        )
    }
}