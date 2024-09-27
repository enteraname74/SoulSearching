package com.github.enteraname74.soulsearching.feature.player.presentation


import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.swipeable
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import com.github.enteraname74.soulsearching.coreui.SoulSearchingContext
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.ext.clickableIf
import com.github.enteraname74.soulsearching.coreui.ext.isDark
import com.github.enteraname74.soulsearching.coreui.ext.toDp
import com.github.enteraname74.soulsearching.coreui.navigation.SoulBackHandler
import com.github.enteraname74.soulsearching.coreui.theme.color.AnimatedColorPaletteBuilder
import com.github.enteraname74.soulsearching.coreui.theme.color.LocalColors
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.theme.color.animated
import com.github.enteraname74.soulsearching.coreui.utils.*
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerEvent
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerNavigationState
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerUiUtils
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerViewModel
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerMusicListViewManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.player.presentation.composable.PlayerMinimisedMainInfo
import com.github.enteraname74.soulsearching.feature.player.presentation.composable.PlayerMusicCover
import com.github.enteraname74.soulsearching.feature.player.presentation.composable.PlayerTopInformation
import com.github.enteraname74.soulsearching.feature.player.presentation.composable.playercontrols.ExpandedPlayerControlsComposable
import com.github.enteraname74.soulsearching.feature.playerpanel.PlayerPanelDraggableView
import com.github.enteraname74.soulsearching.feature.playerpanel.composable.PlayerPanelContent
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import com.github.enteraname74.soulsearching.theme.orDefault
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.ranges.coerceIn

@Suppress("Deprecation")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlayerDraggableView(
    maxHeight: Float,
    navigateToAlbum: (String) -> Unit,
    navigateToArtist: (String) -> Unit,
    navigateToModifyMusic: (String) -> Unit,
    playerViewModel: PlayerViewModel,
    playbackManager: PlaybackManager = injectElement(),
    colorThemeManager: ColorThemeManager = injectElement(),
    playerViewManager: PlayerViewManager = injectElement(),
    playerMusicListViewManager: PlayerMusicListViewManager = injectElement(),
) {
    val coroutineScope = rememberCoroutineScope()
    val state by playerViewModel.state.collectAsState()

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

    // If no music is being played, and the player view is still shown, we need to hide it.
    if (state.playedList.isEmpty() &&
        playerViewManager.currentValue != BottomSheetStates.COLLAPSED &&
        !playerViewManager.isAnimationRunning
    ) {
        coroutineScope.launch {
            if (playerMusicListViewManager.currentValue != BottomSheetStates.COLLAPSED) {
                playerMusicListViewManager.animateTo(
                    newState = BottomSheetStates.COLLAPSED,
                )
            }
            playerViewManager.animateTo(
                newState = BottomSheetStates.COLLAPSED,
            )
        }
    }

    if (playerViewManager.currentValue == BottomSheetStates.COLLAPSED
        && !playerViewManager.isAnimationRunning && playbackManager.currentMusic != null
    ) {
        println("4")
        playbackManager.stopPlayback()
    }

    val alphaTransition = getAlphaTransition()

    CompositionLocalProvider(
        LocalColors provides AnimatedColorPaletteBuilder.animate(playerColorTheme.orDefault())
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
            val animatedBackgroundColor =
                if (playerViewManager.currentValue == BottomSheetStates.EXPANDED) {
                    SoulSearchingColorTheme.colorScheme.primary
                } else {
                    SoulSearchingColorTheme.colorScheme.secondary
                }.animated(label = PLAYER_BACKGROUND_COLOR_LABEL)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = animatedBackgroundColor
                    )
                    .clickableIf(enabled = playerViewManager.currentValue == BottomSheetStates.MINIMISED) {
                        coroutineScope.launch {
                            playerViewManager.animateTo(
                                newState = BottomSheetStates.EXPANDED,
                            )
                        }
                    }
                    .align(Alignment.TopStart)
            ) {

                val imageSize = getImageSize()
                var playerTopInformationHeight by rememberSaveable { mutableStateOf(0) }

                PlayerTopInformation(
                    modifier = Modifier
                        .align(Alignment.TopStart),
                    alphaTransition = alphaTransition,
                    state = state,
                    playerViewModel = playerViewModel,
                    navigateToArtist = navigateToArtist,
                    navigateToAlbum = navigateToAlbum,
                    onTopInformationHeightChange = { height ->
                        playerTopInformationHeight = height
                    },
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

                val playerControlsWidth: Dp = getPlayerControlsWidth(
                    imageSize = imageSize,
                )
                val imageHorizontalPadding = getImageHorizontalPadding(imageSize)
                val imageTopPadding = getImageTopPadding(
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
                            state.currentMusic?.let {
                                playerViewModel.showMusicBottomSheet(it)
                            }
                        },
                        canSwipeCover = state.canSwipeCover,
                    )

                    if (!PlayerUiUtils.canShowRowControlPanel()) {
                        Box(
                            modifier = Modifier
                                .padding(
                                    top = getTopInformationBottomPadding(),
                                )
                                .width(controlsBoxWidth),
                            contentAlignment = Alignment.Center,
                        ) {
                            ExpandedPlayerControlsComposable(
                                modifier = Modifier
                                    .width(playerControlsWidth)
                                    .alpha(alphaTransition),
                                onSetFavoriteState = {
                                    playerViewModel.onEvent(
                                        PlayerEvent.ToggleFavoriteState
                                    )
                                },
                                isMusicInFavorite = state.isCurrentMusicInFavorite,
                                currentMusicPosition = state.currentMusicPosition,
                                playerMode = state.playerMode,
                                isPlaying = state.isPlaying
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
                            onSetFavoriteState = {
                                playerViewModel.onEvent(
                                    PlayerEvent.ToggleFavoriteState
                                )
                            },
                            isMusicInFavorite = state.isCurrentMusicInFavorite,
                            currentMusicPosition = state.currentMusicPosition,
                            playerMode = state.playerMode,
                            isPlaying = state.isPlaying
                        )

                        if (PlayerUiUtils.canShowSidePanel()) {
                            PlayerPanelContent(
                                playerState = state,
                                onSelectedMusic = playerViewModel::showMusicBottomSheet,
                                onRetrieveLyrics = {
                                    playerViewModel.onEvent(
                                        PlayerEvent.GetLyrics,
                                    )
                                },
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
                        onSelectedMusic = playerViewModel::showMusicBottomSheet,
                        onRetrieveLyrics = {
                            playerViewModel.onEvent(
                                PlayerEvent.GetLyrics
                            )
                        },
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
                            onSelectedMusic = playerViewModel::showMusicBottomSheet,
                            onRetrieveLyrics = {
                                playerViewModel.onEvent(
                                    PlayerEvent.GetLyrics,
                                )
                            },
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
                )
            }
        }
    }
}


@Composable
private fun getTransitionRatio(
    playerViewManager: PlayerViewManager = injectElement(),
): Float {
    val maxHeight: Float = rememberWindowHeight()
    val currentOffset = playerViewManager.offset

    val maxOffset: Float = maxHeight - PlayerMinimisedHeight
    return currentOffset / maxOffset
}

@Composable
private fun getAlphaTransition(): Float {
    val ratio = getTransitionRatio()
    val alpha = 1f - ratio
    val fixedAlpha = alpha.coerceIn(
        minimumValue = 0f,
        maximumValue = 1f,
    )
    return fixedAlpha
}

@Composable
private fun getImageSize(): Dp {
    val maxHeight: Dp = rememberWindowHeightDp()
    val ratio = getTransitionRatio()

    val maxImageSize = max(
        maxHeight / 2.45f,
        MinImageSize,
    )

    val size = max(
        maxImageSize * (1f - ratio),
        MinImageSize,
    )
    val coerced = size.coerceIn(
        minimumValue = MinImageSize,
        maximumValue = maxImageSize,
    )

    return coerced
}

@Composable
private fun getImageHorizontalPadding(imageSize: Dp): Dp {
    val windowSize = rememberWindowSize()
    if (windowSize != WindowSize.Small) {
        return MinImagePaddingStart
    }

    val maxWidth = rememberWindowWidthDp()
    val ratio = getTransitionRatio()
    val alpha = 1f - ratio

    val padding = max(
        ((maxWidth - imageSize) / 2) * alpha,
        MinImagePaddingStart,
    )

    return padding
}

@Composable
private fun getPlayerControlsWidth(
    imageSize: Dp,
): Dp {
    val maxWidth: Dp = rememberWindowWidthDp()
    return min(
        imageSize + PlayerControlsExtraWidth,
        maxWidth,
    )
}

@Composable
private fun getImageTopPadding(
    expandedMainInformationHeight: Int,
    imageSize: Dp
): Dp =
    if (PlayerUiUtils.canShowRowControlPanel()) {
        getImageTopPaddingForRowView(imageSize)
    } else {
        getImageTopPaddingForColumnView(expandedMainInformationHeight)
    }

@Composable
private fun getTopInformationBottomPadding(): Dp {
    val maxHeight: Dp = rememberWindowHeightDp()
    return maxHeight / 20
}

@Composable
private fun getImageTopPaddingForColumnView(
    expandedMainInformationHeight: Int
): Dp {

    val mainInfoHeightDp: Dp = expandedMainInformationHeight.toDp() + getTopInformationBottomPadding()

    val ratio = getTransitionRatio()
    val alpha = 1f - ratio

    return max(
        mainInfoHeightDp * alpha,
        MinImagePaddingTop
    )
}

@Composable
private fun getImageTopPaddingForRowView(
    imageSize: Dp
): Dp {

    val maxHeight = rememberWindowHeightDp()
    val topPadding = (maxHeight - imageSize) / 2

    val ratio = getTransitionRatio()
    val alpha = 1f - ratio

    return max(
        topPadding * alpha,
        MinImagePaddingTop,
    )
}

@Composable
private fun BoxWithConstraintsScope.getSidePanelWidth(playerControlsWidth: Dp): Dp {
    val maxWidth = this.maxWidth
    val maxWidthForPanel = maxWidth - playerControlsWidth

    return maxWidthForPanel.coerceIn(
        minimumValue = MinPlayerSidePanelWidth,
        maximumValue = MaxPlayerSidePanelWidth,
    )
}

private val MinImageSize: Dp
@Composable
get() = UiConstants.CoverSize.small

private val MinImagePaddingStart: Dp = 4.dp
private val MinImagePaddingTop: Dp = 4.dp

private val PlayerControlsExtraWidth: Dp = 25.dp

private val MinPlayerSidePanelWidth: Dp = 50.dp
private val MaxPlayerSidePanelWidth: Dp = 600.dp

private val PLAYER_BACKGROUND_COLOR_LABEL = "PLAYER_BACKGROUND_COLOR_LABEL"
