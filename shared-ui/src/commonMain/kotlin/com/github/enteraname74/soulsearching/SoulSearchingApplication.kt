package com.github.enteraname74.soulsearching

import androidx.compose.animation.core.tween
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.CrossfadeTransition
import com.github.enteraname74.domain.model.SoulSearchingSettings
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.*
import com.github.enteraname74.soulsearching.coreui.theme.color.ColorThemeManager
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.domain.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.appinit.FetchingMusicsComposable
import com.github.enteraname74.soulsearching.feature.coversprovider.AllImageCoversViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.AllMusicsViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.MainActivityViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.MainPageScreen
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SoulSearchingApplication(
    settings: SoulSearchingSettings = injectElement(),
    playbackManager: PlaybackManager = injectElement(),
    colorThemeManager: ColorThemeManager = injectElement(),
    playerViewManager: PlayerViewManager = injectElement(),
    feedbackPopUpManager: FeedbackPopUpManager = injectElement(),
) {
    val allMusicsViewModel = injectElement<AllMusicsViewModel>()
    val allImageCoversViewModel = injectElement<AllImageCoversViewModel>()
    val mainActivityViewModel = injectElement<MainActivityViewModel>()

    val musicState by allMusicsViewModel.state.collectAsState()
    val coversState by allImageCoversViewModel.state.collectAsState()

    SoulSearchingColorTheme.colorScheme = colorThemeManager.getColorTheme()

    playbackManager.retrieveCoverMethod = allImageCoversViewModel::getImageCover

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

    rememberCoroutineScope()

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

    var generalNavigator: Navigator? by remember { mutableStateOf(null) }

    FeedbackPopUpScaffold(
        feedbackPopUpManager = feedbackPopUpManager,
    ) {
        PlayerViewScaffold(
            generalNavigator = generalNavigator,
        ) {
            var hasLastPlayedMusicsBeenFetched by rememberSaveable {
                mutableStateOf(false)
            }

            if (!hasLastPlayedMusicsBeenFetched) {
                LaunchedEffect(key1 = "FETCH_LAST_PLAYED_LIST") {
                    val playerSavedMusics = playbackManager.getSavedPlayedList()
                    if (playerSavedMusics.isNotEmpty()) {
                        playbackManager.initializePlayerFromSavedList(playerSavedMusics)
                        playerViewManager.animateTo(
                            newState = BottomSheetStates.MINIMISED,
                        )
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
        }
    }
}

