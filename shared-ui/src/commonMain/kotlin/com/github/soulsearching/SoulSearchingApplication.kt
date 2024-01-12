package com.github.soulsearching

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import com.github.soulsearching.composables.MissingPermissionsComposable
import com.github.soulsearching.composables.NavigationHandler
import com.github.soulsearching.composables.appfeatures.FetchingMusicsComposable
import com.github.soulsearching.composables.player.PlayerDraggableView
import com.github.soulsearching.composables.player.PlayerMusicListView
import com.github.soulsearching.composables.remembers.rememberPlayerDraggableState
import com.github.soulsearching.composables.remembers.rememberPlayerMusicDraggableState
import com.github.soulsearching.composables.remembers.rememberSearchDraggableState
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.model.PlaybackManager
import com.github.soulsearching.model.settings.SoulSearchingSettings
import com.github.soulsearching.navigation.Route
import com.github.soulsearching.navigation.RoutesNames
import com.github.soulsearching.theme.ColorThemeManager
import com.github.soulsearching.types.BottomSheetStates
import com.github.soulsearching.utils.ColorPaletteUtils
import com.github.soulsearching.utils.PlayerUtils
import com.github.soulsearching.viewmodel.AllAlbumsViewModel
import com.github.soulsearching.viewmodel.AllArtistsViewModel
import com.github.soulsearching.viewmodel.AllImageCoversViewModel
import com.github.soulsearching.viewmodel.AllMusicsViewModel
import com.github.soulsearching.viewmodel.AllPlaylistsViewModel
import com.github.soulsearching.viewmodel.AllQuickAccessViewModel
import com.github.soulsearching.viewmodel.MainActivityViewModel
import com.github.soulsearching.viewmodel.ModifyAlbumViewModel
import com.github.soulsearching.viewmodel.ModifyArtistViewModel
import com.github.soulsearching.viewmodel.ModifyMusicViewModel
import com.github.soulsearching.viewmodel.ModifyPlaylistViewModel
import com.github.soulsearching.viewmodel.NavigationViewModel
import com.github.soulsearching.viewmodel.PlayerMusicListViewModel
import com.github.soulsearching.viewmodel.PlayerViewModel
import com.github.soulsearching.viewmodel.SelectedAlbumViewModel
import com.github.soulsearching.viewmodel.SelectedArtistViewModel
import com.github.soulsearching.viewmodel.SelectedPlaylistViewModel
import com.github.soulsearching.viewmodel.SettingsAddMusicsViewModel
import com.github.soulsearching.viewmodel.SettingsAllFoldersViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SoulSearchingApplication(
    allMusicsViewModel: AllMusicsViewModel,
    allPlaylistsViewModel: AllPlaylistsViewModel,
    allAlbumsViewModel: AllAlbumsViewModel,
    allArtistsViewModel: AllArtistsViewModel,
    allImageCoversViewModel: AllImageCoversViewModel,
    playerMusicListViewModel: PlayerMusicListViewModel,
    allQuickAccessViewModel: AllQuickAccessViewModel,
    settingsAllFoldersViewModel: SettingsAllFoldersViewModel,
    mainActivityViewModel: MainActivityViewModel,
    selectedAlbumViewModel: SelectedAlbumViewModel,
    selectedArtistViewModel: SelectedArtistViewModel,
    selectedPlaylistViewModel: SelectedPlaylistViewModel,
    modifyAlbumViewModel: ModifyAlbumViewModel,
    modifyArtistViewModel: ModifyArtistViewModel,
    modifyMusicViewModel: ModifyMusicViewModel,
    modifyPlaylistViewModel: ModifyPlaylistViewModel,
    settingsAddMusicsViewModel: SettingsAddMusicsViewModel,
    navigationViewModel: NavigationViewModel,
    colorThemeManager: ColorThemeManager,
    settings: SoulSearchingSettings,
    playbackManager: PlaybackManager,
    playerViewModel: PlayerViewModel
) {
    settings.initializeSorts(
        onMusicEvent = allMusicsViewModel.handler::onMusicEvent,
        onPlaylistEvent = allPlaylistsViewModel.handler::onPlaylistEvent,
        onArtistEvent = allArtistsViewModel.handler::onArtistEvent,
        onAlbumEvent = allAlbumsViewModel.handler::onAlbumEvent
    )

    PlayerUtils.playerViewModel = playerViewModel
    with(PlayerUtils.playerViewModel.handler) {
        retrieveCoverMethod = allImageCoversViewModel.handler::getImageCover
        updateNbPlayed = { allMusicsViewModel.handler.onMusicEvent(MusicEvent.AddNbPlayed(it)) }
    }


    val playlistState by allPlaylistsViewModel.handler.state.collectAsState()
    val albumState by allAlbumsViewModel.handler.state.collectAsState()
    val artistState by allArtistsViewModel.handler.state.collectAsState()
    val musicState by allMusicsViewModel.handler.state.collectAsState()
    val coversState by allImageCoversViewModel.handler.state.collectAsState()
    val playerMusicListState by playerMusicListViewModel.handler.state.collectAsState()
    val playerMusicState by PlayerUtils.playerViewModel.handler.state.collectAsState()
    val quickAccessState by allQuickAccessViewModel.handler.state.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    colorThemeManager.currentColorPalette = ColorPaletteUtils.getPaletteFromAlbumArt(
        image = PlayerUtils.playerViewModel.handler.currentMusicCover
    )

    if (!mainActivityViewModel.handler.isReadPermissionGranted || !mainActivityViewModel.handler.isPostNotificationGranted) {
        MissingPermissionsComposable()
        return
    }
    if (!mainActivityViewModel.handler.hasMusicsBeenFetched) {
        FetchingMusicsComposable(
            finishAddingMusicsAction = {
                settings.setBoolean(
                    SoulSearchingSettings.HAS_MUSICS_BEEN_FETCHED_KEY,
                    true
                )
                mainActivityViewModel.handler.hasMusicsBeenFetched = true
            },
            allMusicsViewModel = allMusicsViewModel
        )
        return
    }

    if (coversState.covers.isNotEmpty() && !mainActivityViewModel.handler.cleanImagesLaunched) {
        LaunchedEffect(key1 = "Launch") {
            CoroutineScope(Dispatchers.IO).launch {
                for (cover in coversState.covers) {
                    allImageCoversViewModel.handler.deleteImageIsNotUsed(cover)
                }
            }

            CoroutineScope(Dispatchers.IO).launch {
                if (PlayerUtils.playerViewModel.handler.currentMusic != null) {
                    PlayerUtils.playerViewModel.handler.defineCoverAndPaletteFromCoverId(
                        coverId = PlayerUtils.playerViewModel.handler.currentMusic?.coverId
                    )
                    playbackManager.updateNotification()
                }
            }
            mainActivityViewModel.handler.cleanImagesLaunched = true
        }
    }

    if (musicState.musics.isNotEmpty() && !mainActivityViewModel.handler.cleanMusicsLaunched) {
        allMusicsViewModel.handler.checkAndDeleteMusicIfNotExist()
        mainActivityViewModel.handler.cleanMusicsLaunched = true
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val navigationController = navigationViewModel.handler.navigationController
        val constraintsScope = this
        val maxHeight = with(LocalDensity.current) {
            constraintsScope.maxHeight.toPx()
        }

        val playerDraggableState = rememberPlayerDraggableState(
            constraintsScope = constraintsScope
        )

        val musicListDraggableState = rememberPlayerMusicDraggableState(
            constraintsScope = constraintsScope
        )
        val searchDraggableState = rememberSearchDraggableState(
            constraintsScope = constraintsScope
        )

        if (!mainActivityViewModel.handler.hasLastPlayedMusicsBeenFetched) {
            LaunchedEffect(key1 = "FETCH_LAST_PLAYED_LIST") {
                val playerSavedMusics =
                    playerMusicListViewModel.handler.getPlayerMusicList()
                if (playerSavedMusics.isNotEmpty()) {
                    playbackManager.initializePlayerFromSavedList(playerSavedMusics)
                    coroutineScope.launch {
                        playerDraggableState.state.animateTo(BottomSheetStates.MINIMISED)
                    }
                }
                mainActivityViewModel.handler.hasLastPlayedMusicsBeenFetched = true
            }
        }

        NavigationHandler(
            navigationController = navigationController,
            playerDraggableState = playerDraggableState,
            searchDraggableState = searchDraggableState,
            musicState = musicState,
            playlistState = playlistState,
            albumState = albumState,
            artistState = artistState,
            quickAccessState = quickAccessState,
            allPlaylistsViewModel = allPlaylistsViewModel,
            allImageCoversViewModel = allImageCoversViewModel,
            modifyArtistViewModel = modifyArtistViewModel,
            modifyAlbumViewModel = modifyAlbumViewModel,
            modifyMusicViewModel = modifyMusicViewModel,
            modifyPlaylistViewModel = modifyPlaylistViewModel,
            selectedArtistViewModel = selectedArtistViewModel,
            selectedAlbumViewModel = selectedAlbumViewModel,
            selectedPlaylistViewModel = selectedPlaylistViewModel,
            settingsAllFoldersViewModel = settingsAllFoldersViewModel,
            addMusicsViewModel = settingsAddMusicsViewModel,
            allArtistsViewModel = allArtistsViewModel,
            allAlbumsViewModel = allAlbumsViewModel,
            allMusicsViewModel = allMusicsViewModel,
            allFoldersViewModel = settingsAllFoldersViewModel,
            playerMusicListViewModel = playerMusicListViewModel,
            playerMusicListDraggableState = musicListDraggableState,
        )

        PlayerDraggableView(
            maxHeight = maxHeight,
            draggableState = playerDraggableState,
            retrieveCoverMethod = allImageCoversViewModel.handler::getImageCover,
            musicListDraggableState = musicListDraggableState,
            playerMusicListViewModel = playerMusicListViewModel,
            onMusicEvent = PlayerUtils.playerViewModel.handler::onMusicEvent,
            isMusicInFavoriteMethod = allMusicsViewModel.handler::isMusicInFavorite,
            navigateToArtist = {
                navigationController.navigateTo(
                    route = Route(
                        route = RoutesNames.SELECTED_ARTIST_SCREEN,
                        arguments = mapOf(Pair("artistId", it))
                    )
                )
            },
            navigateToAlbum = {
                navigationController.navigateTo(
                    route = Route(
                        route = RoutesNames.SELECTED_ALBUM_SCREEN,
                        arguments = mapOf(Pair("albumId", it))
                    )
                )
            },
            retrieveAlbumIdMethod = {
                allMusicsViewModel.handler.getAlbumIdFromMusicId(it)
            },
            retrieveArtistIdMethod = {
                allMusicsViewModel.handler.getArtistIdFromMusicId(it)
            },
            musicState = playerMusicState,
            playlistState = playlistState,
            onPlaylistEvent = allPlaylistsViewModel.handler::onPlaylistEvent,
            navigateToModifyMusic = {
                navigationController.navigateTo(
                    route = Route(
                        route = RoutesNames.MODIFY_MUSIC_SCREEN,
                        arguments = mapOf(Pair("musicId", it))
                    )
                )
            },
            playbackManager = playbackManager
        )

        PlayerMusicListView(
            coverList = coversState.covers,
            musicState = playerMusicListState,
            playlistState = playlistState,
            onMusicEvent = playerMusicListViewModel.handler::onMusicEvent,
            onPlaylistEvent = allPlaylistsViewModel.handler::onPlaylistEvent,
            navigateToModifyMusic = {
                navigationController.navigateTo(
                    route = Route(
                        route = RoutesNames.MODIFY_MUSIC_SCREEN,
                        arguments = mapOf(Pair("musicId", it))
                    )
                )
            },
            musicListDraggableState = musicListDraggableState,
            playerDraggableState = playerDraggableState,
            playerMusicListViewModel = playerMusicListViewModel
        )
    }
}

