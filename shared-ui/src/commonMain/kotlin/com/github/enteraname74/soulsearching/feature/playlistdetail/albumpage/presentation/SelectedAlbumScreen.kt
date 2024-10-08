package com.github.enteraname74.soulsearching.feature.playlistdetail.albumpage.presentation

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.ext.isPreviousScreenAPlaylistDetails
import com.github.enteraname74.soulsearching.ext.safePush
import com.github.enteraname74.soulsearching.feature.playlistdetail.albumpage.domain.SelectedAlbumNavigationState
import com.github.enteraname74.soulsearching.feature.playlistdetail.albumpage.domain.SelectedAlbumState
import com.github.enteraname74.soulsearching.feature.playlistdetail.albumpage.domain.SelectedAlbumViewModel
import com.github.enteraname74.soulsearching.feature.playlistdetail.artistpage.presentation.SelectedArtistScreen
import com.github.enteraname74.soulsearching.feature.playlistdetail.composable.PlaylistScreen
import com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.presentation.ModifyAlbumScreen
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.presentation.ModifyMusicScreen
import com.github.enteraname74.soulsearching.feature.playlistdetail.composable.PlaylistDetailScreen
import java.util.*

/**
 * Represent the view of the selected album screen.
 */
data class SelectedAlbumScreen(
    private val selectedAlbumId: String,
) : PlaylistDetailScreen(selectedAlbumId) {
    private val albumId: UUID = UUID.fromString(selectedAlbumId)

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<SelectedAlbumViewModel>()

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
                    navigator.safePush(ModifyMusicScreen(selectedMusicId = selectedMusic.musicId.toString()))
                    screenModel.consumeNavigation()
                }
                SelectedAlbumNavigationState.ToEdit -> {
                    navigator.safePush(ModifyAlbumScreen(selectedAlbumId = selectedAlbumId))
                    screenModel.consumeNavigation()
                }

                is SelectedAlbumNavigationState.ToArtist -> {
                    val selectedArtistId = (navigationState as SelectedAlbumNavigationState.ToArtist).artistId
                    navigator.safePush(SelectedArtistScreen(selectedArtistId = selectedArtistId.toString()))
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
                if (!navigator.isPreviousScreenAPlaylistDetails()) {
                    colorThemeManager.removePlaylistTheme()
                }
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