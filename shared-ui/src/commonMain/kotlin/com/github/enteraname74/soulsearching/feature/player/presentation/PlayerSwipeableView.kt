package com.github.enteraname74.soulsearching.feature.player.presentation


import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.swipeable
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import com.github.enteraname74.soulsearching.coreui.SoulSearchingContext
import com.github.enteraname74.soulsearching.coreui.ext.isDark
import com.github.enteraname74.soulsearching.coreui.navigation.SoulBackHandler
import com.github.enteraname74.soulsearching.coreui.theme.color.AnimatedColorPaletteBuilder
import com.github.enteraname74.soulsearching.coreui.theme.color.LocalColors
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.utils.PlayerMinimisedHeight
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerNavigationState
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerUiUtils
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerViewModel
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerViewState
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerMusicListViewManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.player.presentation.screen.PlayerSwipeableDataScreen
import com.github.enteraname74.soulsearching.feature.player.presentation.screen.PlayerSwipeableLoadingScreen
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import com.github.enteraname74.soulsearching.theme.orDefault
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.roundToInt

@Suppress("Deprecation")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlayerDraggableView(
    maxHeight: Float,
    navigateToAlbum: (String) -> Unit,
    navigateToArtist: (String) -> Unit,
    navigateToModifyMusic: (String) -> Unit,
    playerViewModel: PlayerViewModel,
    colorThemeManager: ColorThemeManager = injectElement(),
    playerViewManager: PlayerViewManager = injectElement(),
    playerMusicListViewManager: PlayerMusicListViewManager = injectElement(),
) {
    val coroutineScope = rememberCoroutineScope()

    val state by playerViewModel.state.collectAsState()
    val currentMusicProgressionState by playerViewModel.currentSongProgressionState.collectAsState()
    val bottomSheetState by playerViewModel.bottomSheetState.collectAsState()
    val dialogState by playerViewModel.dialogState.collectAsState()
    val navigationState by playerViewModel.navigationState.collectAsState()
    val addToPlaylistBottomSheet by playerViewModel.addToPlaylistBottomSheet.collectAsState()
    val playerColorTheme by colorThemeManager.playerColorTheme.collectAsState()

    bottomSheetState?.BottomSheet()
    dialogState?.Dialog()
    addToPlaylistBottomSheet?.BottomSheet()

    val canShowPanel = PlayerUiUtils.canShowSidePanel()
    val shouldCloseMusicListDraggableView: () -> Boolean = {
        !canShowPanel && playerMusicListViewManager.currentValue != BottomSheetStates.COLLAPSED
    }

    LaunchedEffect(navigationState) {
        when (navigationState) {
            PlayerNavigationState.Idle -> { /*no-op*/
            }

            is PlayerNavigationState.ToModifyMusic -> {
                coroutineScope.launch {
                    if (shouldCloseMusicListDraggableView()) {
                        playerMusicListViewManager.animateTo(newState = BottomSheetStates.COLLAPSED)
                    }
                    playerViewManager.animateTo(newState = BottomSheetStates.MINIMISED)
                }.invokeOnCompletion {
                    val selectedMusic = (navigationState as PlayerNavigationState.ToModifyMusic).music
                    navigateToModifyMusic(selectedMusic.musicId.toString())
                    playerViewModel.consumeNavigation()
                }
            }

            is PlayerNavigationState.ToAlbum -> {
                val albumId = (navigationState as? PlayerNavigationState.ToAlbum)?.albumId ?: return@LaunchedEffect
                navigateToAlbum(albumId.toString())
            }
            is PlayerNavigationState.ToArtist -> {
                val artistId = (navigationState as? PlayerNavigationState.ToArtist)?.artistId ?: return@LaunchedEffect
                navigateToArtist(artistId.toString())
            }
        }
    }

    SoulBackHandler(playerViewManager.currentValue == BottomSheetStates.EXPANDED) {
        coroutineScope.launch {
            if (playerMusicListViewManager.currentValue != BottomSheetStates.COLLAPSED) {
                playerMusicListViewManager.animateTo(
                    newState = BottomSheetStates.COLLAPSED,
                )
            }
            playerViewManager.animateTo(
                newState = BottomSheetStates.MINIMISED,
            )
        }
    }

    CompositionLocalProvider(
        LocalColors provides AnimatedColorPaletteBuilder.animate(palette = playerColorTheme.orDefault())
    ) {

        SoulSearchingContext.setSystemBarsColor(
            statusBarColor = Color.Transparent,
            navigationBarColor = Color.Transparent,
            isUsingDarkIcons = !SoulSearchingColorTheme.colorScheme.primary.isDark()
        )

        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = 0,
                        y = max(playerViewManager.offset.roundToInt(), 0)
                    )
                }
                .swipeable(
                    state = playerViewManager.playerDraggableState,
                    orientation = Orientation.Vertical,
                    anchors = mapOf(
                        (maxHeight - PlayerMinimisedHeight) to BottomSheetStates.MINIMISED,
                        maxHeight to BottomSheetStates.COLLAPSED,
                        0f to BottomSheetStates.EXPANDED
                    )
                )
        ) {
            when(state) {
                PlayerViewState.Closed -> {
                    PlayerSwipeableLoadingScreen()

                    // We ensure to keep the player view closed:
                    coroutineScope.launch {
                        if (playerMusicListViewManager.currentValue != BottomSheetStates.COLLAPSED) {
                            playerMusicListViewManager.animateTo(
                                newState = BottomSheetStates.COLLAPSED,
                            )
                        }
                        if (playerViewManager.currentValue != BottomSheetStates.COLLAPSED) {
                            playerViewManager.animateTo(
                                newState = BottomSheetStates.COLLAPSED
                            )
                        }
                    }
                }
                is PlayerViewState.Data -> {
                    var hasViewBeenShown by rememberSaveable {
                        mutableStateOf(false)
                    }

                    PlayerSwipeableDataScreen(
                        maxHeight = maxHeight,
                        state = state as PlayerViewState.Data,
                        onArtistClicked = {
                            coroutineScope.launch {
                                playerViewManager.animateTo(newState = BottomSheetStates.MINIMISED)
                            }
                            playerViewModel.navigateToArtist()
                        },
                        onAlbumClicked = {
                            coroutineScope.launch {
                                playerViewManager.animateTo(newState = BottomSheetStates.MINIMISED)
                            }
                            playerViewModel.navigateToAlbum()
                        },
                        showMusicBottomSheet = playerViewModel::showMusicBottomSheet,
                        toggleFavoriteState = playerViewModel::toggleFavoriteState,
                        seekTo = playerViewModel::seekTo,
                        changePlayerMode = playerViewModel::changePlayerMode,
                        previous = playerViewModel::previous,
                        next = playerViewModel::next,
                        updateCover = playerViewModel::setCurrentMusicCover,
                        togglePlayPause = playerViewModel::togglePlayPause,
                        onRetrieveLyrics = playerViewModel::setLyricsOfCurrentMusic,
                        currentMusicProgression = currentMusicProgressionState,
                    )

                    LaunchedEffect(playerViewManager.currentValue) {
                        if (playerViewManager.currentValue == BottomSheetStates.COLLAPSED) {
                            if (hasViewBeenShown) {
                                playerViewModel.stopPlayback()
                            }
                        }
                        hasViewBeenShown = true
                    }

                    LaunchedEffect(
                        (state as PlayerViewState.Data).initPlayerWithMinimiseView
                    ) {
                        if (!playerViewManager.isAnimationRunning && ((state as PlayerViewState.Data).initPlayerWithMinimiseView)) {
                            coroutineScope.launch {
                                playerViewManager.animateTo(BottomSheetStates.MINIMISED)
                            }.invokeOnCompletion {
                                hasViewBeenShown = true
                            }
                        }
                    }
                }
            }
        }
    }
}
