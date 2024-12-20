package com.github.enteraname74.soulsearching.feature.player.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.ext.blend
import com.github.enteraname74.soulsearching.coreui.ext.clickableIf
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionState
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.theme.color.animated
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerUiUtils
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerUiUtils.MaxPlayerSidePanelWidth
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerUiUtils.MinPlayerSidePanelWidth
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlayerViewState
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerMusicListViewManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlayerViewSettingsState
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
    settingsState: PlayerViewSettingsState,
    currentMusicProgression: Int,
    onArtistClicked: (selectedArtist: Artist) -> Unit,
    onAlbumClicked: () -> Unit,
    closeSelection: () -> Unit,
    showMusicBottomSheet: (music: Music) -> Unit,
    onLongSelectOnMusic: (Music) -> Unit,
    multiSelectionState: MultiSelectionState,
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

    LaunchedEffect(playerViewManager.currentValue) {
        if (playerViewManager.currentValue != BottomSheetStates.EXPANDED) {
            closeSelection()
        }
    }


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


        AnimatedVisibility(
            visible = settingsState.isMinimisedSongProgressionShown
        ) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(1f - alphaTransition)
                    .height(SONG_PROGRESSION_HEIGHT),
                progress = { (currentMusicProgression.toFloat() / state.currentMusic.duration.toFloat()).coerceIn(0f,1f) },
                color = SoulSearchingColorTheme.colorScheme.onSecondary,
                trackColor = SoulSearchingColorTheme.colorScheme.subSecondaryText.blend(
                    other = SoulSearchingColorTheme.colorScheme.primary,
                    ratio = 0.5f,
                ),
                drawStopIndicator = {},
                strokeCap = StrokeCap.Square,
                gapSize = 0.dp,
            )
        }

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
            },
            onSongInfoClicked = {
                showMusicBottomSheet(state.currentMusic)
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
                canSwipeCover = settingsState.canSwipeCover,
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
                        onMoreClickedOnMusic = showMusicBottomSheet,
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
                        multiSelectionState = multiSelectionState,
                        onLongSelectOnMusic = onLongSelectOnMusic,
                    )
                }
            }
        }


        if (!PlayerUiUtils.canShowSidePanel()) {
            PlayerPanelDraggableView(
                maxHeight = maxHeight,
                playerState = state,
                onMoreClickedOnMusic = showMusicBottomSheet,
                onRetrieveLyrics = onRetrieveLyrics,
                secondaryColor = SoulSearchingColorTheme.colorScheme.secondary,
                textColor = SoulSearchingColorTheme.colorScheme.onSecondary,
                subTextColor = SoulSearchingColorTheme.colorScheme.subSecondaryText,
                buttonColors = SoulButtonDefaults.primaryColors(),
                multiSelectionState = multiSelectionState,
                onLongSelectOnMusic = onLongSelectOnMusic,
                closeSelection = closeSelection,
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
                    onMoreClickedOnMusic = showMusicBottomSheet,
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
                    onLongSelectOnMusic = onLongSelectOnMusic,
                    multiSelectionState = multiSelectionState,
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

private val SONG_PROGRESSION_HEIGHT: Dp = 2.dp