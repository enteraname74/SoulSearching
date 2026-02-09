package com.github.enteraname74.soulsearching.feature.player.presentation


import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material.ExperimentalMaterialApi
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.swipeable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import com.github.enteraname74.soulsearching.coreui.SoulSearchingContext
import com.github.enteraname74.soulsearching.coreui.ext.isDark
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionScaffold
import com.github.enteraname74.soulsearching.coreui.multiselection.SelectionMode
import com.github.enteraname74.soulsearching.coreui.navigation.SoulBackHandler
import com.github.enteraname74.soulsearching.coreui.theme.color.AnimatedColorPaletteBuilder
import com.github.enteraname74.soulsearching.coreui.theme.color.LocalColors
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBarDefaults
import com.github.enteraname74.soulsearching.coreui.utils.PlayerMinimisedHeight
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlayerNavigationState
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerUiUtils
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerViewModel
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlayerViewState
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerMusicListViewManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.player.presentation.screen.PlayerSwipeableDataScreen
import com.github.enteraname74.soulsearching.feature.player.presentation.screen.PlayerSwipeableLoadingScreen
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import com.github.enteraname74.soulsearching.theme.orDefault
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.max
import kotlin.math.roundToInt

@Suppress("Deprecation")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlayerDraggableView(
    maxHeight: Float,
    navigateToAlbum: (UUID) -> Unit,
    navigateToArtist: (UUID) -> Unit,
    navigateToModifyMusic: (UUID) -> Unit,
    navigateToRemoteLyricsSettings: () -> Unit,
    playerViewModel: PlayerViewModel,
    colorThemeManager: ColorThemeManager = injectElement(),
    playerViewManager: PlayerViewManager = injectElement(),
    playerMusicListViewManager: PlayerMusicListViewManager = injectElement(),
) {
    val coroutineScope = rememberCoroutineScope()

    val state by playerViewModel.state.collectAsState()
    val lyricsState by playerViewModel.lyricsState.collectAsState()
    val settingsState by playerViewModel.viewSettingsState.collectAsState()
    val multiSelectionState by playerViewModel.multiSelectionState.collectAsState()

    val currentMusicProgressionState by playerViewModel.currentSongProgressionState.collectAsState()
    val bottomSheetState by playerViewModel.bottomSheetState.collectAsState()
    val dialogState by playerViewModel.dialogState.collectAsState()
    val navigationState by playerViewModel.navigationState.collectAsState()
    val addToPlaylistBottomSheet by playerViewModel.addToPlaylistBottomSheet.collectAsState()
    val playerColorTheme by colorThemeManager.playerColorTheme.collectAsState()

    bottomSheetState?.BottomSheet()
    dialogState?.Dialog()
    addToPlaylistBottomSheet?.BottomSheet()

    val previousDraggableState by playerViewManager.previousState.collectAsState()

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
                    navigateToModifyMusic(selectedMusic.musicId)
                    playerViewModel.consumeNavigation()
                }
            }

            is PlayerNavigationState.ToAlbum -> {
                val albumId = (navigationState as? PlayerNavigationState.ToAlbum)?.albumId ?: return@LaunchedEffect
                navigateToAlbum(albumId)
                playerViewModel.consumeNavigation()
            }

            is PlayerNavigationState.ToArtist -> {
                val artistId = (navigationState as? PlayerNavigationState.ToArtist)?.artistId ?: return@LaunchedEffect
                navigateToArtist(artistId)
                playerViewModel.consumeNavigation()
            }

            PlayerNavigationState.ToRemoteLyricsSettings -> {
                coroutineScope.launch {
                    if (shouldCloseMusicListDraggableView()) {
                        playerMusicListViewManager.animateTo(newState = BottomSheetStates.COLLAPSED)
                    }
                    playerViewManager.animateTo(newState = BottomSheetStates.MINIMISED)
                }.invokeOnCompletion {
                    navigateToRemoteLyricsSettings()
                    playerViewModel.consumeNavigation()
                }
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

        MultiSelectionScaffold(
            multiSelectionManagerImpl = playerViewModel.multiSelectionManagerImpl,
            onCancel = playerViewModel::clearMultiSelection,
            onMore = playerViewModel::handleMultiSelectionBottomSheet,
            topBarColors = if (PlayerUiUtils.canShowSidePanel()) {
                SoulTopBarDefaults.secondary()
            } else {
                SoulTopBarDefaults.primary()
            }
        ) {
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
                when (state) {
                    PlayerViewState.Closed -> {
                        PlayerSwipeableLoadingScreen()

                        LaunchedEffect(previousDraggableState) {
                            if (previousDraggableState != null) {
                                playerViewManager.consumePreviousState()
                            }
                        }

                        LaunchedEffect(playerMusicListViewManager.currentValue) {
                            if (playerMusicListViewManager.currentValue != BottomSheetStates.COLLAPSED) {
                                coroutineScope.launch {
                                    playerMusicListViewManager.animateTo(
                                        newState = BottomSheetStates.COLLAPSED,
                                    )
                                }
                            }
                        }

                        LaunchedEffect(playerViewManager.currentValue) {
                            if (playerViewManager.currentValue != BottomSheetStates.COLLAPSED) {
                                coroutineScope.launch {
                                    playerViewManager.animateTo(
                                        newState = BottomSheetStates.COLLAPSED
                                    )
                                }
                            }
                        }
                    }

                    is PlayerViewState.Data -> {
                        PlayerSwipeableDataScreen(
                            maxHeight = maxHeight,
                            state = state as PlayerViewState.Data,
                            lyricsState = lyricsState,
                            onArtistClicked = {
                                coroutineScope.launch {
                                    playerViewManager.animateTo(newState = BottomSheetStates.MINIMISED)
                                }
                                playerViewModel.navigateToArtist(it)
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
                            currentMusicProgression = currentMusicProgressionState,
                            settingsState = settingsState,
                            onLongSelectOnMusic = {
                                playerViewModel.toggleElementInSelection(
                                    id = it.musicId,
                                    mode = SelectionMode.Music,
                                )
                            },
                            multiSelectionState = multiSelectionState,
                            closeSelection = playerViewModel::clearMultiSelection,
                            onActivateRemoteLyrics = playerViewModel::navigateToRemoteLyricsSettings,
                        )

                        /*
                        If the previous state was expanded/minimised and the current one is collapsed,
                        then it indicates that the playback should stop (user action for example).
                         */
                        if ((previousDraggableState != BottomSheetStates.COLLAPSED && previousDraggableState != null) && playerViewManager.currentValue == BottomSheetStates.COLLAPSED) {
                            LaunchedEffect(Unit) {
                                playerViewModel.stopPlayback()
                            }
                        } else if ((previousDraggableState == BottomSheetStates.COLLAPSED || previousDraggableState == null) && playerViewManager.currentValue == BottomSheetStates.COLLAPSED) {
                            // In this case, the playback is on but the view has not been shown
                            LaunchedEffect(Unit) {
                                coroutineScope.launch {
                                    playerViewManager.animateTo(
                                        if ((state as PlayerViewState.Data).initPlayerWithMinimiseView) {
                                            BottomSheetStates.MINIMISED
                                        } else {
                                            BottomSheetStates.EXPANDED
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
