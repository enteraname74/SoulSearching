package com.github.soulsearching

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.CrossfadeTransition
import com.github.soulsearching.appinit.presentation.FetchingMusicsComposable
import com.github.soulsearching.colortheme.domain.model.SoulSearchingColorTheme
import com.github.soulsearching.domain.di.injectElement
import com.github.soulsearching.domain.model.settings.SoulSearchingSettings
import com.github.soulsearching.domain.model.types.BottomSheetStates
import com.github.soulsearching.domain.viewmodel.AllImageCoversViewModel
import com.github.soulsearching.domain.viewmodel.AllMusicsViewModel
import com.github.soulsearching.domain.viewmodel.MainActivityViewModel
import com.github.soulsearching.domain.viewmodel.PlayerMusicListViewModel
import com.github.soulsearching.domain.viewmodel.PlayerViewModel
import com.github.soulsearching.elementpage.albumpage.presentation.SelectedAlbumScreen
import com.github.soulsearching.elementpage.artistpage.presentation.SelectedArtistScreen
import com.github.soulsearching.mainpage.presentation.MainPageScreen
import com.github.soulsearching.modifyelement.modifymusic.presentation.ModifyMusicScreen
import com.github.soulsearching.playedlist.presentation.PlayerMusicListView
import com.github.soulsearching.player.domain.model.PlaybackManager
import com.github.soulsearching.player.presentation.PlayerDraggableView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SoulSearchingApplication(
    settings: SoulSearchingSettings = injectElement(),
    playbackManager: PlaybackManager = injectElement(),
) {
    val allMusicsViewModel = injectElement<AllMusicsViewModel>()
    val allImageCoversViewModel = injectElement<AllImageCoversViewModel>()
    val playerViewModel = injectElement<PlayerViewModel>()
    val playerMusicListViewModel = injectElement<PlayerMusicListViewModel>()
    val mainActivityViewModel = injectElement<MainActivityViewModel>()

    val musicState by allMusicsViewModel.handler.state.collectAsState()
    val playerMusicListState by playerMusicListViewModel.handler.state.collectAsState()

    val coversState by allImageCoversViewModel.handler.state.collectAsState()

    val playerDraggableState = playerViewModel.handler.playerDraggableState
    val playerState by playerViewModel.handler.state.collectAsState()
    val musicListDraggableState = playerMusicListViewModel.handler.musicListDraggableState

    if (coversState.covers.isNotEmpty() && !mainActivityViewModel.handler.cleanImagesLaunched) {
        LaunchedEffect("Covers check") {
            CoroutineScope(Dispatchers.IO).launch {
                for (cover in coversState.covers) {
                    allImageCoversViewModel.handler.deleteImageIfNotUsed(cover)
                }
            }

            CoroutineScope(Dispatchers.IO).launch {
                playbackManager.currentMusic?.let { currentMusic ->
                    playbackManager.defineCoverAndPaletteFromCoverId(
                        coverId = currentMusic.coverId
                    )
                    playbackManager.update()
                }
            }
            mainActivityViewModel.handler.cleanImagesLaunched = true
        }
    }

    val coroutineScope = rememberCoroutineScope()


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


    if (musicState.musics.isNotEmpty() && !mainActivityViewModel.handler.cleanMusicsLaunched) {
        allMusicsViewModel.handler.checkAndDeleteMusicIfNotExist()
        mainActivityViewModel.handler.cleanMusicsLaunched = true
    }

    var generalNavigator: Navigator? = null

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = SoulSearchingColorTheme.colorScheme.primary
            )
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
                        playerDraggableState.animateTo(
                            BottomSheetStates.MINIMISED,
                            tween(Constants.AnimationDuration.normal)
                        )
                    }
                }
                mainActivityViewModel.handler.hasLastPlayedMusicsBeenFetched = true
            }
        }

        Navigator(MainPageScreen()) { navigator ->
            generalNavigator = navigator

            CrossfadeTransition(
                navigator = navigator,
                animationSpec = tween(Constants.AnimationDuration.normal)
            ) { screen ->
                screen.Content()
            }
        }


        PlayerDraggableView(
            maxHeight = maxHeight,
            draggableState = playerDraggableState,
            playerMusicListViewModel = playerMusicListViewModel,
            retrieveCoverMethod = allImageCoversViewModel.handler::getImageCover,
            musicListDraggableState = musicListDraggableState,
            navigateToAlbum = { albumId ->
                generalNavigator?.push(
                    SelectedAlbumScreen(selectedAlbumId = albumId)
                )
            },
            navigateToArtist = { artistId ->
                generalNavigator?.push(
                    SelectedArtistScreen(selectedArtistId = artistId)
                )
            },
            retrieveArtistIdMethod = {
                allMusicsViewModel.handler.getArtistIdFromMusicId(it)
            },
            retrieveAlbumIdMethod = {
                allMusicsViewModel.handler.getAlbumIdFromMusicId(it)
            },
            navigateToModifyMusic = { musicId ->
                generalNavigator?.push(
                    ModifyMusicScreen(
                        selectedMusicId = musicId
                    )
                )
            },
            playerViewModel = playerViewModel
        )

        PlayerMusicListView(
            maxHeight = maxHeight,
            coverList = coversState.covers,
            musicState = playerMusicListState,
            onMusicEvent = playerMusicListViewModel.handler::onMusicEvent,
            navigateToModifyMusic = { musicId ->
                generalNavigator?.push(
                    ModifyMusicScreen(
                        selectedMusicId = musicId.toString()
                    )
                )
            },
            musicListDraggableState = musicListDraggableState,
            playerDraggableState = playerDraggableState,
//            playerMusicListViewModel = playerMusicListViewModel,
            playedList = playerState.playedList
        )
    }
}

