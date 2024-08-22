package com.github.enteraname74.soulsearching.feature.playlistdetail.artistpage.presentation

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.feature.playlistdetail.albumpage.presentation.SelectedAlbumScreen
import com.github.enteraname74.soulsearching.feature.playlistdetail.artistpage.domain.SelectedArtistNavigationState
import com.github.enteraname74.soulsearching.feature.playlistdetail.artistpage.domain.SelectedArtistState
import com.github.enteraname74.soulsearching.feature.playlistdetail.artistpage.domain.SelectedArtistViewModel
import com.github.enteraname74.soulsearching.feature.playlistdetail.artistpage.presentation.composable.ArtistAlbums
import com.github.enteraname74.soulsearching.feature.playlistdetail.composable.PlaylistScreen
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistDetailScreen
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyalbum.presentation.ModifyAlbumScreen
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyartist.presentation.ModifyArtistScreen
import com.github.enteraname74.soulsearching.feature.modifyelement.modifymusic.presentation.ModifyMusicScreen
import java.util.*

/**
 * Represent the view of the selected artist screen.
 */
data class SelectedArtistScreen(
    private val selectedArtistId: String
) : Screen, PlaylistDetailScreen {
    override val key: ScreenKey = selectedArtistId
    private val artistId = UUID.fromString(selectedArtistId)

    @Composable
    override fun Content() {
        val screenModel = getScreenModel<SelectedArtistViewModel>()

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
            when (navigationState) {
                SelectedArtistNavigationState.Idle -> { /*no-op*/
                }

                is SelectedArtistNavigationState.ToModifyAlbum -> {
                    val selectedAlbum = (navigationState as SelectedArtistNavigationState.ToModifyAlbum).album
                    navigator.push(ModifyAlbumScreen(selectedAlbumId = selectedAlbum.albumId.toString()))
                    screenModel.consumeNavigation()
                }

                is SelectedArtistNavigationState.ToModifyMusic -> {
                    val selectedMusic = (navigationState as SelectedArtistNavigationState.ToModifyMusic).music
                    navigator.push(ModifyMusicScreen(selectedMusicId = selectedMusic.musicId.toString()))
                    screenModel.consumeNavigation()
                }

                is SelectedArtistNavigationState.ToAlbum -> {
                    val albumId = (navigationState as SelectedArtistNavigationState.ToAlbum).albumId
                    navigator.push(SelectedAlbumScreen(selectedAlbumId = albumId.toString()))
                    screenModel.consumeNavigation()
                }

                SelectedArtistNavigationState.ToEdit -> {
                    navigator.push(ModifyArtistScreen(selectedArtistId = selectedArtistId))
                    screenModel.consumeNavigation()
                }
            }
        }

        var isArtistFetched by rememberSaveable {
            mutableStateOf(false)
        }

        LaunchedEffect(isArtistFetched) {
            if (!isArtistFetched) {
                screenModel.init(artistId = artistId)
                isArtistFetched = true
            }
        }

        SelectedArtistScreenView(
            selectedArtistViewModel = screenModel,
            navigateBack = {
                colorThemeManager.removePlaylistTheme()
                navigator.pop()
            },
        )
    }
}

@Composable
fun SelectedArtistScreenView(
    selectedArtistViewModel: SelectedArtistViewModel,
    navigateBack: () -> Unit,
) {
    val state by selectedArtistViewModel.state.collectAsState()

    when (state) {
        is SelectedArtistState.Data -> {
            val dataState = (state as SelectedArtistState.Data)
            PlaylistScreen(
                playlistDetail = dataState.playlistDetail,
                playlistDetailListener = selectedArtistViewModel,
                navigateBack = navigateBack,
                onShowMusicBottomSheet = selectedArtistViewModel::showMusicBottomSheet,
                optionalContent = {
                    ArtistAlbums(
                        albums = dataState.artistAlbums,
                        onAlbumClick = selectedArtistViewModel::toAlbum,
                        onAlbumLongClick = selectedArtistViewModel::showAlbumBottomSheet,
                    )
                }
            )
        }
        SelectedArtistState.Loading -> SoulLoadingScreen(
            navigateBack = navigateBack,
        )
    }
}