package com.github.soulsearching

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberSwipeableState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import com.github.soulsearching.composables.MissingPermissionsComposable
import com.github.soulsearching.composables.NavigationHandler
import com.github.soulsearching.composables.appfeatures.FetchingMusicsComposable
import com.github.soulsearching.composables.player.PlayerDraggableView
import com.github.soulsearching.composables.player.PlayerMusicListView
import com.github.soulsearching.di.injectElement
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.model.PlaybackManager
import com.github.soulsearching.model.settings.SoulSearchingSettings
import com.github.soulsearching.navigation.Route
import com.github.soulsearching.navigation.RoutesNames
import com.github.soulsearching.theme.ColorThemeManager
import com.github.soulsearching.types.BottomSheetStates
import com.github.soulsearching.utils.ColorPaletteUtils
import com.github.soulsearching.utils.PlayerUtils
import com.github.soulsearching.viewmodel.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
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
    colorThemeManager: ColorThemeManager = injectElement(),
    settings: SoulSearchingSettings = injectElement(),
    playbackManager: PlaybackManager,
    playerViewModel: PlayerViewModel
) {
    settings.initializeSorts(
        onMusicEvent = allMusicsViewModel.handler::onMusicEvent,
        onPlaylistEvent = allPlaylistsViewModel.handler::onPlaylistEvent,
        onArtistEvent = allArtistsViewModel.handler::onArtistEvent,
        onAlbumEvent = allAlbumsViewModel.handler::onAlbumEvent
    )

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

        val playerDraggableState = rememberSwipeableState(
            initialValue = BottomSheetStates.COLLAPSED
        )

        val musicListDraggableState = rememberSwipeableState(
            initialValue = BottomSheetStates.COLLAPSED
        )
        val searchDraggableState = rememberSwipeableState(
            initialValue = BottomSheetStates.COLLAPSED
        )

        if (!mainActivityViewModel.handler.hasLastPlayedMusicsBeenFetched) {
            LaunchedEffect(key1 = "FETCH_LAST_PLAYED_LIST") {
                val playerSavedMusics =
                    playerMusicListViewModel.handler.getPlayerMusicList()
                if (playerSavedMusics.isNotEmpty()) {
                    playbackManager.initializePlayerFromSavedList(playerSavedMusics)
                    coroutineScope.launch {
                        playerDraggableState.animateTo(BottomSheetStates.MINIMISED, tween(Constants.AnimationDuration.normal))
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
            playerMusicListViewModel = playerMusicListViewModel,
            maxHeight = maxHeight
        )
    }
}

