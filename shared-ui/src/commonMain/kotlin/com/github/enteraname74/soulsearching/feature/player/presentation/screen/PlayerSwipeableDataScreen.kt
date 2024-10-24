package com.github.enteraname74.soulsearching.feature.player.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.Dp
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.ext.clickableIf
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.theme.color.animated
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerUiUtils
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerUiUtils.MaxPlayerSidePanelWidth
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerUiUtils.MinPlayerSidePanelWidth
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerViewState
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerMusicListViewManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.player.presentation.composable.PlayerMinimisedMainInfo
import com.github.enteraname74.soulsearching.feature.player.presentation.composable.PlayerMusicCover
import com.github.enteraname74.soulsearching.feature.player.presentation.composable.PlayerTopInformation
import com.github.enteraname74.soulsearching.feature.player.presentation.composable.playercontrols.ExpandedPlayerControlsComposable
import com.github.enteraname74.soulsearching.feature.playerpanel.PlayerPanelDraggableView
import com.github.enteraname74.soulsearching.feature.playerpanel.composable.PlayerPanelContent
import kotlinx.coroutines.launch

@Composable
fun BoxScope.PlayerSwipeableDataScreen(
    maxHeight: Float,
    state: PlayerViewState.Data,
    currentMusicProgression: Int,
    onArtistClicked: () -> Unit,
    onAlbumClicked: () -> Unit,
    showMusicBottomSheet: (music: Music) -> Unit,
    toggleFavoriteState: () -> Unit,
    seekTo: (newPosition: Int) -> Unit,
    changePlayerMode: () -> Unit,
    previous: () -> Unit,
    togglePlayPause: () -> Unit,
    next: () -> Unit,
    onRetrieveLyrics: () -> Unit,
    updateCover: (ImageBitmap?) -> Unit,
    playerViewManager: PlayerViewManager = injectElement(),
    playerMusicListViewManager: PlayerMusicListViewManager = injectElement(),
) {

    val coroutineScope = rememberCoroutineScope()
    val alphaTransition = PlayerUiUtils.getAlphaTransition()

    val animatedBackgroundColor =
        if (playerViewManager.currentValue == BottomSheetStates.EXPANDED) {
            SoulSearchingColorTheme.colorScheme.primary
        } else {
            SoulSearchingColorTheme.colorScheme.secondary
        }.animated(label = PlayerUiUtils.PLAYER_BACKGROUND_COLOR_LABEL)

    var bitmap: ImageBitmap? by remember {
        mutableStateOf(null)
    }

    val onCoverLoaded: (ImageBitmap?) -> Unit = {
        bitmap = it
    }

    LaunchedEffect(bitmap) {
        updateCover(bitmap)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = animatedBackgroundColor
            )
            .padding(paddingValues = WindowInsets.navigationBars.asPaddingValues())
            .clickableIf(enabled = playerViewManager.currentValue == BottomSheetStates.MINIMISED) {
                coroutineScope.launch {
                    playerViewManager.animateTo(
                        newState = BottomSheetStates.EXPANDED,
                    )
                }
            }
            .align(Alignment.TopStart)
    ) {

        val imageSize = PlayerUiUtils.getImageSize()
        var playerTopInformationHeight by rememberSaveable { mutableStateOf(0) }

        PlayerTopInformation(
            modifier = Modifier
                .align(Alignment.TopStart),
            alphaTransition = alphaTransition,
            state = state,
            onTopInformationHeightChange = { height ->
                playerTopInformationHeight = height
            },
            onArtistClicked = onArtistClicked,
            onAlbumClicked = onAlbumClicked,
            onShowPanel = if (
                !PlayerUiUtils.canShowSidePanel()
                && PlayerUiUtils.getDraggablePanelCollapsedOffset() == 0f
            ) {
                {
                    coroutineScope.launch {
                        playerMusicListViewManager.animateTo(
                            newState = BottomSheetStates.EXPANDED,
                        )
                    }
                }
            } else {
                null
            }
        )

        val playerControlsWidth: Dp = PlayerUiUtils.getPlayerControlsWidth(
            imageSize = imageSize,
        )
        val imageHorizontalPadding = PlayerUiUtils.getImageHorizontalPadding(imageSize)
        val imageTopPadding = PlayerUiUtils.getImageTopPadding(
            expandedMainInformationHeight = playerTopInformationHeight,
            imageSize = imageSize,
        )
        val fullImageSize = imageSize + (imageHorizontalPadding * 2)
        Column {
            val controlsBoxWidth = playerControlsWidth + (imageHorizontalPadding * 2)

            PlayerMusicCover(
                imageSize = imageSize,
                horizontalPadding = imageHorizontalPadding,
                topPadding = imageTopPadding,
                onLongClick = {
                    showMusicBottomSheet(state.currentMusic)
                },
                canSwipeCover = state.canSwipeCover,
                aroundSongs = state.aroundSongs,
                onCoverLoaded = onCoverLoaded,
                currentMusic = state.currentMusic,
            )

            if (!PlayerUiUtils.canShowRowControlPanel()) {
                Box(
                    modifier = Modifier
                        .padding(
                            top = PlayerUiUtils.getTopInformationBottomPadding(),
                        )
                        .width(controlsBoxWidth),
                    contentAlignment = Alignment.Center,
                ) {
                    ExpandedPlayerControlsComposable(
                        modifier = Modifier
                            .width(playerControlsWidth)
                            .alpha(alphaTransition),
                        toggleFavoriteState = toggleFavoriteState,
                        state = state,
                        next = next,
                        previous = previous,
                        changePlayerMode = changePlayerMode,
                        seekTo = seekTo,
                        togglePlayPause = togglePlayPause,
                        currentMusicProgression = currentMusicProgression,
                    )
                }
            }
        }

        if (PlayerUiUtils.canShowRowControlPanel()) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = fullImageSize,
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                ExpandedPlayerControlsComposable(
                    modifier = Modifier
                        .weight(
                            weight = 1f,
                            fill = false,
                        )
                        .alpha(alphaTransition)
                        .padding(
                            horizontal = UiConstants.Spacing.medium,
                        ),
                    toggleFavoriteState = toggleFavoriteState,
                    state = state,
                    next = next,
                    previous = previous,
                    changePlayerMode = changePlayerMode,
                    seekTo = seekTo,
                    togglePlayPause = togglePlayPause,
                    currentMusicProgression = currentMusicProgression,
                )

                if (PlayerUiUtils.canShowSidePanel()) {
                    PlayerPanelContent(
                        playerState = state,
                        onSelectedMusic = showMusicBottomSheet,
                        onRetrieveLyrics = onRetrieveLyrics,
                        textColor = SoulSearchingColorTheme.colorScheme.onPrimary,
                        subTextColor = SoulSearchingColorTheme.colorScheme.subSecondaryText,
                        isExpanded = playerViewManager.currentValue == BottomSheetStates.EXPANDED,
                        buttonColors = SoulButtonDefaults.secondaryColors(),
                        modifier = Modifier
                            .padding(
                                top = imageTopPadding,
                                start = UiConstants.Spacing.medium,
                            )
                            .alpha(alphaTransition)
                            .weight(1f)
                            .widthIn(
                                min = MinPlayerSidePanelWidth,
                                max = MaxPlayerSidePanelWidth,
                            ),
                    )
                }
            }
        }


        if (!PlayerUiUtils.canShowSidePanel()) {
            PlayerPanelDraggableView(
                maxHeight = maxHeight,
                playerState = state,
                onSelectedMusic = showMusicBottomSheet,
                onRetrieveLyrics = onRetrieveLyrics,
                secondaryColor = SoulSearchingColorTheme.colorScheme.secondary,
                textColor = SoulSearchingColorTheme.colorScheme.onSecondary,
                subTextColor = SoulSearchingColorTheme.colorScheme.subSecondaryText,
                buttonColors = SoulButtonDefaults.primaryColors(),
            )
        } else if (!PlayerUiUtils.canShowRowControlPanel()) {
            BoxWithConstraints(
                modifier = Modifier
                    .padding(
                        top = imageTopPadding,
                        start = UiConstants.Spacing.medium,
                    )
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopEnd,
            ) {
                PlayerPanelContent(
                    playerState = state,
                    onSelectedMusic = showMusicBottomSheet,
                    onRetrieveLyrics = onRetrieveLyrics,
                    textColor = SoulSearchingColorTheme.colorScheme.onPrimary,
                    subTextColor = SoulSearchingColorTheme.colorScheme.subPrimaryText,
                    isExpanded = playerViewManager.currentValue == BottomSheetStates.EXPANDED,
                    buttonColors = SoulButtonDefaults.secondaryColors(),
                    modifier = Modifier
                        .alpha(alphaTransition)
                        .width(
                            this.getSidePanelWidth(playerControlsWidth = playerControlsWidth)
                        ),
                )
            }
        }

        PlayerMinimisedMainInfo(
            imageSize = imageSize,
            currentMusic = state.currentMusic,
            isPlaying = state.isPlaying,
            alphaTransition = 1f - alphaTransition,
            previous = previous,
            next = next,
            togglePlayPause = togglePlayPause,
        )
    }
}

@Composable
fun BoxWithConstraintsScope.getSidePanelWidth(playerControlsWidth: Dp): Dp {
    val maxWidth = this.maxWidth
    val maxWidthForPanel = maxWidth - playerControlsWidth

    return maxWidthForPanel.coerceIn(
        minimumValue = MinPlayerSidePanelWidth,
        maximumValue = MaxPlayerSidePanelWidth,
    )
}