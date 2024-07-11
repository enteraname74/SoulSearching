package com.github.enteraname74.soulsearching

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.CrossfadeTransition
import com.github.enteraname74.domain.model.SoulSearchingSettings
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.ColorThemeManager
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.domain.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.appinit.FetchingMusicsComposable
import com.github.enteraname74.soulsearching.feature.coversprovider.AllImageCoversViewModel
import com.github.enteraname74.soulsearching.feature.elementpage.albumpage.presentation.SelectedAlbumScreen
import com.github.enteraname74.soulsearching.feature.elementpage.artistpage.presentation.SelectedArtistScreen
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.AllMusicsViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.MainActivityViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.MainPageScreen
import com.github.enteraname74.soulsearching.feature.modifyelement.modifymusic.presentation.ModifyMusicScreen
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerViewModel
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.enteraname74.soulsearching.feature.player.presentation.PlayerDraggableView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SoulSearchingApplication(
    settings: SoulSearchingSettings = injectElement(),
    playbackManager: PlaybackManager = injectElement(),
    colorThemeManager: ColorThemeManager = injectElement()
) {
    val allMusicsViewModel = injectElement<AllMusicsViewModel>()
    val allImageCoversViewModel = injectElement<AllImageCoversViewModel>()
    val playerViewModel = injectElement<PlayerViewModel>()
    val mainActivityViewModel = injectElement<MainActivityViewModel>()

    val musicState by allMusicsViewModel.state.collectAsState()
    val coversState by allImageCoversViewModel.state.collectAsState()

    SoulSearchingColorTheme.colorScheme = colorThemeManager.getColorTheme()

    playbackManager.retrieveCoverMethod = allImageCoversViewModel::getImageCover

    val playerDraggableState = playerViewModel.playerDraggableState
    val musicListDraggableState = playerViewModel.musicListDraggableState

    if (coversState.covers.isNotEmpty() && !mainActivityViewModel.cleanImagesLaunched) {
        LaunchedEffect("Covers check") {
            CoroutineScope(Dispatchers.IO).launch {
                for (cover in coversState.covers) {
                    allImageCoversViewModel.deleteImageIfNotUsed(cover.coverId)
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
            mainActivityViewModel.cleanImagesLaunched = true
        }
    }

    val coroutineScope = rememberCoroutineScope()

    if (!mainActivityViewModel.hasMusicsBeenFetched) {
        FetchingMusicsComposable(
            finishAddingMusicsAction = {
                settings.setBoolean(
                    SoulSearchingSettings.HAS_MUSICS_BEEN_FETCHED_KEY,
                    true
                )
                mainActivityViewModel.hasMusicsBeenFetched = true
            },
            allMusicsViewModel = allMusicsViewModel
        )
        return
    }

    if (musicState.musics.isNotEmpty() && !mainActivityViewModel.cleanMusicsLaunched) {
        allMusicsViewModel.checkAndDeleteMusicIfNotExist()
        mainActivityViewModel.cleanMusicsLaunched = true
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

        var hasLastPlayedMusicsBeenFetched by rememberSaveable {
            mutableStateOf(false)
        }

        if (!hasLastPlayedMusicsBeenFetched) {
            LaunchedEffect(key1 = "FETCH_LAST_PLAYED_LIST") {
                val playerSavedMusics = playbackManager.getSavedPlayedList()
                if (playerSavedMusics.isNotEmpty()) {
                    playbackManager.initializePlayerFromSavedList(playerSavedMusics)
                    coroutineScope.launch {
                        playerDraggableState.animateTo(
                            BottomSheetStates.MINIMISED,
                            tween(UiConstants.AnimationDuration.normal)
                        )
                    }
                }
               hasLastPlayedMusicsBeenFetched = true
            }
        }

        Navigator(MainPageScreen()) { navigator ->
            generalNavigator = navigator

            CrossfadeTransition(
                navigator = navigator,
                animationSpec = tween(UiConstants.AnimationDuration.normal)
            ) { screen ->
                screen.Content()
            }
        }


        PlayerDraggableView(
            maxHeight = maxHeight,
            draggableState = playerDraggableState,
            retrieveCoverMethod = allImageCoversViewModel::getImageCover,
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
                allMusicsViewModel.getArtistIdFromMusicId(it)
            },
            retrieveAlbumIdMethod = {
                allMusicsViewModel.getAlbumIdFromMusicId(it)
            },
            navigateToModifyMusic = { musicId ->
                generalNavigator?.push(
                    ModifyMusicScreen(
                        selectedMusicId = musicId
                    )
                )
            },
            playerViewModel = playerViewModel,
            coverList = coversState.covers
        )
    }
}

