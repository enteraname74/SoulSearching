package com.github.soulsearching

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberSwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import cafe.adriel.voyager.koin.getNavigatorScreenModel
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.CrossfadeTransition
import com.github.soulsearching.composables.MissingPermissionsComposable
import com.github.soulsearching.composables.appfeatures.FetchingMusicsComposable
import com.github.soulsearching.composables.player.PlayerDraggableView
import com.github.soulsearching.composables.player.PlayerMusicListView
import com.github.soulsearching.di.injectElement
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.model.PlaybackManager
import com.github.soulsearching.model.settings.SoulSearchingSettings
import com.github.soulsearching.screens.MainPageScreen
import com.github.soulsearching.screens.ModifyMusicScreen
import com.github.soulsearching.screens.SelectedAlbumScreen
import com.github.soulsearching.screens.SelectedArtistScreen
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
import com.github.soulsearching.viewmodel.PlayerMusicListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SoulSearchingApplication(
    colorThemeManager: ColorThemeManager = injectElement(),
    settings: SoulSearchingSettings = injectElement(),
    playbackManager: PlaybackManager,
) {
    val playerDraggableState = rememberSwipeableState(
        initialValue = BottomSheetStates.COLLAPSED
    )

    val musicListDraggableState = rememberSwipeableState(
        initialValue = BottomSheetStates.COLLAPSED
    )
    val searchDraggableState = rememberSwipeableState(
        initialValue = BottomSheetStates.COLLAPSED
    )

    Navigator(
        MainPageScreen(
            playerDraggableState = playerDraggableState,
            searchDraggableState = searchDraggableState
        )
    ) { navigator ->

        val allMusicsViewModel = navigator.getNavigatorScreenModel<AllMusicsViewModel>()
        val allPlaylistsViewModel = navigator.getNavigatorScreenModel<AllPlaylistsViewModel>()
        val allArtistsViewModel = navigator.getNavigatorScreenModel<AllArtistsViewModel>()
        val allAlbumsViewModel = navigator.getNavigatorScreenModel<AllAlbumsViewModel>()
        val allImageCoversViewModel = navigator.getNavigatorScreenModel<AllImageCoversViewModel>()
        val playerMusicListViewModel = navigator.getNavigatorScreenModel<PlayerMusicListViewModel>()
        val mainActivityViewModel = navigator.getNavigatorScreenModel<MainActivityViewModel>()
        println("Trying : $allMusicsViewModel")

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
        val musicState by allMusicsViewModel.handler.state.collectAsState()
        val coversState by allImageCoversViewModel.handler.state.collectAsState()
        val playerMusicListState by playerMusicListViewModel.handler.state.collectAsState()
        val playerMusicState by PlayerUtils.playerViewModel.handler.state.collectAsState()

        val coroutineScope = rememberCoroutineScope()

        colorThemeManager.currentColorPalette = ColorPaletteUtils.getPaletteFromAlbumArt(
            image = PlayerUtils.playerViewModel.handler.currentMusicCover
        )

        if (!mainActivityViewModel.handler.isReadPermissionGranted || !mainActivityViewModel.handler.isPostNotificationGranted) {
            MissingPermissionsComposable()
            return@Navigator
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
            return@Navigator
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



        CrossfadeTransition(
            navigator = navigator,
            animationSpec = tween(Constants.AnimationDuration.normal)
        ) { screen ->
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                val constraintsScope = this
                val maxHeight = with(LocalDensity.current) {
                    constraintsScope.maxHeight.toPx()
                }

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

                screen.Content()

                PlayerDraggableView(
                    maxHeight = maxHeight,
                    draggableState = playerDraggableState,
                    retrieveCoverMethod = allImageCoversViewModel.handler::getImageCover,
                    musicListDraggableState = musicListDraggableState,
                    playerMusicListViewModel = playerMusicListViewModel,
                    onMusicEvent = PlayerUtils.playerViewModel.handler::onMusicEvent,
                    isMusicInFavoriteMethod = allMusicsViewModel.handler::isMusicInFavorite,
                    navigateToArtist = { artistId ->
                        navigator.push(
                            SelectedArtistScreen(
                                selectedArtistId = artistId,
                                playerDraggableState = playerDraggableState
                            )
                        )
                    },
                    navigateToAlbum = { albumId ->
                        navigator.push(
                            SelectedAlbumScreen(
                                selectedAlbumId = albumId,
                                playerDraggableState = playerDraggableState
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
                    navigateToModifyMusic = { musicId ->
                        navigator.push(
                            ModifyMusicScreen(
                                selectedMusicId = musicId
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
                    navigateToModifyMusic = { musicId ->
                        navigator.push(
                            ModifyMusicScreen(
                                selectedMusicId = musicId
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
    }
}

