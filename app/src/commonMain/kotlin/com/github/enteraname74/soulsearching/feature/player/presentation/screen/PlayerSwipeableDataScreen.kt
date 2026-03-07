package com.github.enteraname74.soulsearching.feature.player.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.ext.blend
import com.github.enteraname74.soulsearching.coreui.ext.clickableIf
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.theme.color.animated
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.multiselection.state.MultiSelectionState
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerUiUtils
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerUiUtils.MaxPlayerSidePanelWidth
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerUiUtils.MinPlayerSidePanelWidth
import com.github.enteraname74.soulsearching.feature.player.domain.model.LyricsFetchState
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerMusicListViewManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlayerViewSettingsState
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlayerViewState
import com.github.enteraname74.soulsearching.feature.player.presentation.composable.PlayerMinimisedMainInfo
import com.github.enteraname74.soulsearching.feature.player.presentation.composable.PlayerMusicCover
import com.github.enteraname74.soulsearching.feature.player.presentation.composable.PlayerTopInformation
import com.github.enteraname74.soulsearching.feature.player.presentation.composable.playercontrols.ExpandedPlayerControlsComposable
import com.github.enteraname74.soulsearching.feature.playerpanel.PlayerPanelDraggableView
import com.github.enteraname74.soulsearching.feature.playerpanel.composable.PlayerPanelContent
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun BoxScope.PlayerSwipeableDataScreen(
    maxHeight: Float,
    state: PlayerViewState.Data,
    lyricsState: LyricsFetchState,
    settingsState: PlayerViewSettingsState,
    currentMusicProgression: Int,
    onArtistClicked: (selectedArtist: Artist) -> Unit,
    onAlbumClicked: () -> Unit,
    closeSelection: () -> Unit,
    showMusicBottomSheet: (musicId: UUID) -> Unit,
    onLongSelectOnMusic: (Music) -> Unit,
    multiSelectionState: MultiSelectionState,
    toggleFavoriteState: () -> Unit,
    seekTo: (newPosition: Int) -> Unit,
    changePlayerMode: () -> Unit,
    previous: () -> Unit,
    togglePlayPause: () -> Unit,
    next: () -> Unit,
    onActivateRemoteLyrics: () -> Unit,
    playerViewManager: PlayerViewManager = injectElement(),
    playerMusicListViewManager: PlayerMusicListViewManager = injectElement(),
) {
    val coroutineScope = rememberCoroutineScope()
    val alphaTransition = PlayerUiUtils.getAlphaTransition()

    val animatedBackgroundColor =
        if (playerViewManager.currentValue == BottomSheetStates.EXPANDED || playerViewManager.targetValue == BottomSheetStates.EXPANDED) {
            SoulSearchingColorTheme.colorScheme.primary
        } else {
            SoulSearchingColorTheme.colorScheme.secondary
        }.animated(label = PlayerUiUtils.PLAYER_BACKGROUND_COLOR_LABEL)

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
        var playerTopInformationHeight by rememberSaveable { mutableIntStateOf(0) }


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
                    playerMusicListViewManager.animateTo(
                        newState = BottomSheetStates.EXPANDED,
                    )
                }
            } else {
                null
            },
            onSongInfoClicked = {
                showMusicBottomSheet(state.currentMusic.musicId)
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
                modifier = Modifier
                    .padding(
                        start = UiConstants.Spacing.small * (1 - alphaTransition)
                    ),
                imageSize = imageSize,
                horizontalPadding = imageHorizontalPadding,
                topPadding = imageTopPadding,
                onLongClick = {
                    showMusicBottomSheet(state.currentMusic.musicId)
                },
                canSwipeCover = settingsState.canSwipeCover,
                aroundSongs = state.aroundSongs,
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
                        lyricsState = lyricsState,
                        onMoreClickedOnMusic = showMusicBottomSheet,
                        contentColor = SoulSearchingColorTheme.colorScheme.onPrimary,
                        containerColor = animatedBackgroundColor,
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
                        onActivateRemoteLyrics = onActivateRemoteLyrics,
                    )
                }
            }
        }


        if (!PlayerUiUtils.canShowSidePanel()) {
            PlayerPanelDraggableView(
                maxHeight = maxHeight,
                playerState = state,
                lyricsState = lyricsState,
                onMoreClickedOnMusic = showMusicBottomSheet,
                containerColor = SoulSearchingColorTheme.colorScheme.secondary,
                textColor = SoulSearchingColorTheme.colorScheme.onSecondary,
                subTextColor = SoulSearchingColorTheme.colorScheme.subSecondaryText,
                buttonColors = SoulButtonDefaults.primaryColors(),
                multiSelectionState = multiSelectionState,
                onLongSelectOnMusic = onLongSelectOnMusic,
                closeSelection = closeSelection,
                onActivateRemoteLyrics = onActivateRemoteLyrics,
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
                    lyricsState = lyricsState,
                    onMoreClickedOnMusic = showMusicBottomSheet,
                    contentColor = SoulSearchingColorTheme.colorScheme.onPrimary,
                    containerColor = animatedBackgroundColor,
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
                    onActivateRemoteLyrics = onActivateRemoteLyrics,
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