package com.github.enteraname74.soulsearching.feature.playlistdetail.artistpage.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.multiselection.SelectionMode
import com.github.enteraname74.soulsearching.coreui.screen.SoulErrorScreen
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.coreui.utils.LaunchInit
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.ext.isPreviousScreenAPlaylistDetails
import com.github.enteraname74.soulsearching.ext.safePush
import com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.presentation.ModifyAlbumScreen
import com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.presentation.ModifyArtistScreen
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.presentation.ModifyMusicScreen
import com.github.enteraname74.soulsearching.feature.playlistdetail.albumpage.presentation.SelectedAlbumScreen
import com.github.enteraname74.soulsearching.feature.playlistdetail.artistpage.domain.SelectedArtistNavigationState
import com.github.enteraname74.soulsearching.feature.playlistdetail.artistpage.domain.SelectedArtistState
import com.github.enteraname74.soulsearching.feature.playlistdetail.artistpage.domain.SelectedArtistViewModel
import com.github.enteraname74.soulsearching.feature.playlistdetail.artistpage.presentation.composable.ArtistAlbums
import com.github.enteraname74.soulsearching.feature.playlistdetail.composable.PlaylistDetailScreen
import com.github.enteraname74.soulsearching.feature.playlistdetail.composable.PlaylistScreen
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import java.util.*

/**
 * Represent the view of the selected artist screen.
 */
data class SelectedArtistScreen(
    private val selectedArtistId: String
) : PlaylistDetailScreen(selectedArtistId) {
    private val artistId = UUID.fromString(selectedArtistId)

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<SelectedArtistViewModel>()

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
                    navigator.safePush(ModifyAlbumScreen(selectedAlbumId = selectedAlbum.albumId.toString()))
                    screenModel.consumeNavigation()
                }

                is SelectedArtistNavigationState.ToModifyMusic -> {
                    val selectedMusic = (navigationState as SelectedArtistNavigationState.ToModifyMusic).music
                    navigator.safePush(ModifyMusicScreen(selectedMusicId = selectedMusic.musicId.toString()))
                    screenModel.consumeNavigation()
                }

                is SelectedArtistNavigationState.ToAlbum -> {
                    val albumId = (navigationState as SelectedArtistNavigationState.ToAlbum).albumId
                    navigator.safePush(SelectedAlbumScreen(selectedAlbumId = albumId.toString()))
                    screenModel.consumeNavigation()
                }

                SelectedArtistNavigationState.ToEdit -> {
                    navigator.safePush(ModifyArtistScreen(selectedArtistId = selectedArtistId))
                    screenModel.consumeNavigation()
                }
            }
        }

        LaunchInit {
            screenModel.init(artistId = artistId)
        }

        SelectedArtistScreenView(
            selectedArtistViewModel = screenModel,
            navigateBack = {
                navigator.pop()
                if (!navigator.isPreviousScreenAPlaylistDetails()) {
                    colorThemeManager.removePlaylistTheme()
                }
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
    val multiSelectionState by selectedArtistViewModel.multiSelectionState.collectAsState()

    when (state) {
        is SelectedArtistState.Data -> {
            val dataState = (state as SelectedArtistState.Data)
            PlaylistScreen(
                playlistDetail = dataState.playlistDetail,
                playlistDetailListener = selectedArtistViewModel,
                navigateBack = navigateBack,
                onShowMusicBottomSheet = selectedArtistViewModel::showMusicBottomSheet,
                optionalContent = {
                    if (dataState.artistAlbums.isNotEmpty()) {
                        ArtistAlbums(
                            albums = dataState.artistAlbums,
                            onAlbumClick = selectedArtistViewModel::toAlbum,
                            onAlbumLongClick = { albumWithMusics ->
                                selectedArtistViewModel.toggleElementInSelection(
                                    id = albumWithMusics.album.albumId,
                                    mode = SelectionMode.Album,
                                )
                            },
                            multiSelectionState = multiSelectionState,
                        )
                    }
                },
                multiSelectionManagerImpl = selectedArtistViewModel.multiSelectionManagerImpl,
                onLongSelectOnMusic = {
                    selectedArtistViewModel.toggleElementInSelection(
                        id = it.musicId,
                        mode = SelectionMode.Music,
                    )
                },
                multiSelectionState = multiSelectionState,
            )
        }
        SelectedArtistState.Loading -> SoulLoadingScreen(
            navigateBack = navigateBack,
        )

        SelectedArtistState.Error -> {
            SoulErrorScreen(
                leftAction = TopBarNavigationAction(navigateBack),
                text = strings.artistDoesNotExists,
            )
        }
    }
}