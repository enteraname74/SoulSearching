package com.github.enteraname74.soulsearching.feature.player.presentation


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.swipeable
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.coreui.ScreenOrientation
import com.github.enteraname74.soulsearching.coreui.SoulSearchingContext
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.image.SoulImage
import com.github.enteraname74.soulsearching.coreui.navigation.SoulBackHandler
import com.github.enteraname74.soulsearching.coreui.theme.color.ColorThemeManager
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.utils.ColorPaletteUtils
import com.github.enteraname74.soulsearching.domain.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.ViewSettingsManager
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerEvent
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerNavigationState
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerViewModel
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerMusicListViewManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.player.presentation.composable.ExpandedPlayButtonsComposable
import com.github.enteraname74.soulsearching.feature.player.presentation.composable.MinimisedPlayButtonsComposable
import com.github.enteraname74.soulsearching.feature.playerpanel.PlayerPanelView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

@Suppress("Deprecation")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun PlayerDraggableView(
    maxHeight: Float,
    retrieveCoverMethod: (UUID?) -> ImageBitmap?,
    navigateToAlbum: (String) -> Unit,
    navigateToArtist: (String) -> Unit,
    navigateToModifyMusic: (String) -> Unit,
    playerViewModel: PlayerViewModel,
    coverList: ArrayList<ImageCover>,
    playbackManager: PlaybackManager = injectElement(),
    colorThemeManager: ColorThemeManager = injectElement(),
    viewSettingsManager: ViewSettingsManager = injectElement(),
    playerViewManager: PlayerViewManager = injectElement(),
    playerMusicListViewManager: PlayerMusicListViewManager = injectElement(),
) {
    val coroutineScope = rememberCoroutineScope()
    val state by playerViewModel.state.collectAsState()

    val bottomSheetState by playerViewModel.bottomSheetState.collectAsState()
    val dialogState by playerViewModel.dialogState.collectAsState()
    val navigationState by playerViewModel.navigationState.collectAsState()
    val addToPlaylistBottomSheet by playerViewModel.addToPlaylistBottomSheet.collectAsState()

    bottomSheetState?.BottomSheet()
    dialogState?.Dialog()
    addToPlaylistBottomSheet?.BottomSheet()

    LaunchedEffect(navigationState) {
        when(navigationState) {
            PlayerNavigationState.Idle -> { /*no-op*/  }
            is PlayerNavigationState.ToModifyMusic -> {
                val selectedMusic = (navigationState as PlayerNavigationState.ToModifyMusic).music
                navigateToModifyMusic(selectedMusic.musicId.toString())
                playerViewModel.consumeNavigation()
            }
        }
    }


    val backgroundColor: Color by animateColorAsState(
        targetValue = when (playerViewManager.currentValue) {
            BottomSheetStates.MINIMISED, BottomSheetStates.COLLAPSED -> SoulSearchingColorTheme.colorScheme.secondary
            BottomSheetStates.EXPANDED -> {
                if (colorThemeManager.isPersonalizedDynamicPlayerThemeOn()) {
                    ColorPaletteUtils.getDynamicPrimaryColor(
                        baseColor = colorThemeManager.currentColorPalette?.rgb
                    )
                } else {
                    SoulSearchingColorTheme.defaultTheme.primary
                }
            }
        },
        tween(UiConstants.AnimationDuration.normal),
        label = "BACKGROUND_COLOR_PLAYER_DRAGGABLE_VIEW"
    )
    val textColor: Color by animateColorAsState(
        targetValue = when (playerViewManager.currentValue) {
            BottomSheetStates.COLLAPSED, BottomSheetStates.MINIMISED -> SoulSearchingColorTheme.colorScheme.onPrimary
            BottomSheetStates.EXPANDED -> {
                if (colorThemeManager.isPersonalizedDynamicPlayerThemeOn() && state.currentMusicCover != null) {
                    Color.White
                } else {
                    SoulSearchingColorTheme.defaultTheme.onPrimary
                }
            }
        },
        tween(UiConstants.AnimationDuration.normal),
        label = "TEXT_COLOR_COLOR_PLAYER_DRAGGABLE_VIEW"
    )

    val subTextColor: Color by animateColorAsState(
        targetValue = when (playerViewManager.currentValue) {
            BottomSheetStates.COLLAPSED, BottomSheetStates.MINIMISED -> SoulSearchingColorTheme.colorScheme.subText
            BottomSheetStates.EXPANDED -> {
                if (colorThemeManager.isPersonalizedDynamicPlayerThemeOn() && state.currentMusicCover != null) {
                    Color.LightGray
                } else {
                    SoulSearchingColorTheme.defaultTheme.subText
                }
            }
        },
        tween(UiConstants.AnimationDuration.normal),
        label = "SUB_TEXT_COLOR_COLOR_PLAYER_DRAGGABLE_VIEW"
    )


    val contentColor: Color by animateColorAsState(
        targetValue = when (playerViewManager.currentValue) {
            BottomSheetStates.COLLAPSED, BottomSheetStates.MINIMISED -> SoulSearchingColorTheme.colorScheme.secondary
            BottomSheetStates.EXPANDED -> {
                if (colorThemeManager.isPersonalizedDynamicPlayerThemeOn()) {
                    ColorPaletteUtils.getDynamicSecondaryColor(
                        baseColor = colorThemeManager.currentColorPalette?.rgb
                    )
                } else {
                    SoulSearchingColorTheme.defaultTheme.secondary
                }
            }
        },
        tween(UiConstants.AnimationDuration.normal),
        label = "CONTENT_COLOR_COLOR_PLAYER_DRAGGABLE_VIEW"
    )

    val statusBarColor: Color by animateColorAsState(
        targetValue = when (playerViewManager.currentValue) {
            BottomSheetStates.MINIMISED, BottomSheetStates.COLLAPSED -> SoulSearchingColorTheme.colorScheme.primary
            BottomSheetStates.EXPANDED -> {
                if (colorThemeManager.isPersonalizedDynamicPlayerThemeOn()) {
                    ColorPaletteUtils.getDynamicPrimaryColor(
                        baseColor = colorThemeManager.currentColorPalette?.rgb
                    )
                } else {
                    SoulSearchingColorTheme.defaultTheme.primary
                }
            }
        },
        tween(UiConstants.AnimationDuration.normal),
        label = "STATUS_BAR_COLOR_COLOR_PLAYER_DRAGGABLE_VIEW"
    )

    val navigationBarColor: Color by animateColorAsState(
        targetValue = when (playerViewManager.currentValue) {
            BottomSheetStates.COLLAPSED -> SoulSearchingColorTheme.colorScheme.primary
            BottomSheetStates.MINIMISED -> SoulSearchingColorTheme.colorScheme.secondary
            BottomSheetStates.EXPANDED -> {
                if (colorThemeManager.isPersonalizedDynamicPlayerThemeOn()) {
                    ColorPaletteUtils.getDynamicSecondaryColor(
                        baseColor = colorThemeManager.currentColorPalette?.rgb
                    )
                } else {
                    SoulSearchingColorTheme.defaultTheme.secondary
                }
            }
        }, tween(UiConstants.AnimationDuration.normal),
        label = "NAVIGATION_BAR_COLOR_COLOR_PLAYER_DRAGGABLE_VIEW"
    )

    val isUsingDarkIcons = if (colorThemeManager.currentColorPalette == null
        || !colorThemeManager.isPersonalizedDynamicPlayerThemeOn()
    ) {
        !isSystemInDarkTheme()
    } else {
        false
    }

    SoulSearchingContext.setSystemBarsColor(
        statusBarColor = statusBarColor,
        navigationBarColor = navigationBarColor,
        isUsingDarkIcons = isUsingDarkIcons
    )

    SoulBackHandler(playerViewManager.currentValue == BottomSheetStates.EXPANDED) {
        coroutineScope.launch {
            if (playerMusicListViewManager.currentValue != BottomSheetStates.COLLAPSED) {
                playerMusicListViewManager.animateTo(
                    newState = BottomSheetStates.COLLAPSED,
                )
            }
            playerMusicListViewManager.animateTo(
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
        && !playerViewManager.isAnimationRunning
    ) {
        playbackManager.stopPlayback()
    }

    val orientation = SoulSearchingContext.orientation
    val alphaTransition =
        if (playerViewManager.currentValue == BottomSheetStates.MINIMISED
            && playerViewManager.offset == 0f) {
            0f
        } else {
            when (orientation) {
                ScreenOrientation.HORIZONTAL -> {
                    if ((1f / (abs(playerViewManager.offset) / 70)) > 0.1) {
                        (1f / (abs(playerViewManager.offset) / 70)).coerceAtMost(1f)
                    } else {
                        0f
                    }
                }

                else -> {
                    if ((1f / (abs(
                            max(
                                playerViewManager.offset.roundToInt(),
                                0
                            )
                        ) / 100)) > 0.1
                    ) {
                        (1f / (abs(max(playerViewManager.offset.roundToInt(), 0)) / 100))
                            .coerceAtMost(1f)
                    } else {
                        0f
                    }
                }
            }
        }
    val localDensity = LocalDensity.current
    val playerHeight: Float = with(localDensity) {
        100.dp.toPx()
    }

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
                    (maxHeight - playerHeight) to BottomSheetStates.MINIMISED,
                    maxHeight to BottomSheetStates.COLLAPSED,
                    0f to BottomSheetStates.EXPANDED
                )
            )
    ) {
        val mainBoxClickableModifier =
            if (playerViewManager.currentValue == BottomSheetStates.MINIMISED) {
                Modifier.clickable {
                    coroutineScope.launch {
                        playerViewManager.animateTo(
                            newState = BottomSheetStates.EXPANDED,
                        )
                    }
                }
            } else {
                Modifier
            }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(color = backgroundColor)
                .composed {
                    mainBoxClickableModifier
                }
                .align(Alignment.TopStart)
        ) {
            val constraintsScope = this
            val maxWidth = with(LocalDensity.current) {
                constraintsScope.maxWidth.toPx()
            }

            val imagePaddingStart =
                when (SoulSearchingContext.orientation) {
                    ScreenOrientation.HORIZONTAL -> max(
                        (((maxWidth * 1.5) / 100) - (max(
                            playerViewManager.offset.roundToInt(),
                            0
                        ) / 15)).roundToInt().dp,
                        UiConstants.Spacing.small
                    )

                    else -> max(
                        (((maxWidth * 3.5) / 100) - (max(
                            playerViewManager.offset.roundToInt(),
                            0
                        ) / 40)).roundToInt().dp,
                        UiConstants.Spacing.small
                    )
                }

            val imagePaddingTop =
                when (SoulSearchingContext.orientation) {
                    ScreenOrientation.HORIZONTAL -> max(
                        (((maxHeight * 7) / 100) - (max(
                            playerViewManager.offset.roundToInt(),
                            0
                        ) / 5)).roundToInt().dp,
                        UiConstants.Spacing.small
                    )

                    else -> max(
                        (((maxHeight * 5) / 100) - (max(
                            playerViewManager.offset.roundToInt(),
                            0
                        ) / 15)).roundToInt().dp,
                        UiConstants.Spacing.small
                    )
                }


            val imageSize =
                when (SoulSearchingContext.orientation) {
                    ScreenOrientation.HORIZONTAL -> max(
                        (((maxWidth * 10) / 100) - (max(
                            playerViewManager.offset.roundToInt(),
                            0
                        ) / 2)).dp,
                        55.dp
                    )

                    else -> max(
                        (((maxWidth * 29) / 100) - (max(
                            playerViewManager.offset.roundToInt(),
                            0
                        ) / 7)).dp,
                        55.dp
                    )
                }

            val imageModifier = if (playerViewManager.currentValue == BottomSheetStates.EXPANDED) {
                Modifier.combinedClickable(
                    onLongClick = {
                        state.currentMusic?.let {
                            playerViewModel.showMusicBottomSheet(it)
                        }
                    },
                    onClick = { }
                )
            } else {
                Modifier
            }

            val backImageClickableModifier =
                if (playerViewManager.currentValue != BottomSheetStates.EXPANDED) {
                    Modifier
                } else {
                    Modifier.clickable {
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
                }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(UiConstants.Spacing.small)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = imagePaddingStart,
                            top = imagePaddingTop,
                            end = imagePaddingStart
                        )
                ) {

                    var aroundSongs by remember {
                        mutableStateOf(listOf<Music?>())
                    }
                    aroundSongs = getAroundSongs(playbackManager = playbackManager)

                    if (
                        aroundSongs.filterNotNull().size > 1
                        && playerViewManager.currentValue == BottomSheetStates.EXPANDED
                        && viewSettingsManager.isPlayerSwipeEnabled
                    ) {
                        val pagerState = remember(aroundSongs) {
                            object : PagerState(currentPage = 1) {
                                override val pageCount: Int = aroundSongs.size
                            }
                        }

                        LaunchedEffect(pagerState) {
                            snapshotFlow { pagerState.currentPage }.collect { page ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    when (page) {
                                        0 -> playbackManager.previous()
                                        2 -> playbackManager.next()
                                    }
                                }
                            }
                        }

                        HorizontalPager(
                            state = pagerState,
                            pageSpacing = 120.dp
                        ) { currentSongPos ->

                            SoulImage(
                                modifier = imageModifier,
                                bitmap =
                                retrieveCoverMethod(aroundSongs.getOrNull(currentSongPos)?.coverId),
                                size = imageSize,
                                roundedPercent = (playerViewManager.offset / 100).roundToInt()
                                    .coerceIn(3, 10)
                            )
                        }
                    } else {
                        SoulImage(
                            modifier = imageModifier,
                            bitmap =
                            retrieveCoverMethod(playbackManager.currentMusic?.coverId),
                            size = imageSize,
                            roundedPercent = (playerViewManager.offset / 100).roundToInt()
                                .coerceIn(3, 10)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(alphaTransition),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = "",
                            modifier = Modifier
                                .size(UiConstants.ImageSize.medium)
                                .composed { backImageClickableModifier },
                            colorFilter = ColorFilter.tint(textColor),
                            alpha = alphaTransition
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = state.currentMusic?.name ?: "",
                                color = textColor,
                                maxLines = 1,
                                textAlign = TextAlign.Center,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .basicMarquee()
                            )

                            val clickableArtistModifier =
                                if (playerViewManager.currentValue == BottomSheetStates.EXPANDED) {
                                    Modifier.clickable {
                                        playbackManager.currentMusic?.let {
                                            coroutineScope.launch {
                                                val artistId = withContext(Dispatchers.IO) {
                                                    playerViewModel.getArtistIdFromMusicId(it.musicId)
                                                }
                                                artistId?.let { id ->
                                                    if (playerViewManager.currentValue == BottomSheetStates.EXPANDED) {
                                                        navigateToArtist(id.toString())

                                                        playerViewManager.animateTo(
                                                            newState = BottomSheetStates.MINIMISED,
                                                        )
                                                    }
                                                }

                                            }
                                        }
                                    }
                                } else {
                                    Modifier
                                }

                            val clickableAlbumModifier =
                                if (playerViewManager.currentValue == BottomSheetStates.EXPANDED) {
                                    Modifier.clickable {
                                        playbackManager.currentMusic?.let {
                                            coroutineScope.launch {
                                                val albumId = withContext(Dispatchers.IO) {
                                                    playerViewModel.getAlbumIdFromMusicId(it.musicId)
                                                }
                                                albumId?.let { id ->
                                                    if (playerViewManager.currentValue == BottomSheetStates.EXPANDED) {
                                                        navigateToAlbum(id.toString())

                                                        playerViewManager.animateTo(
                                                            newState = BottomSheetStates.MINIMISED,
                                                        )
                                                    }
                                                }

                                            }
                                        }
                                    }
                                } else {
                                    Modifier
                                }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (state.currentMusic != null) formatTextForEllipsis(
                                        state.currentMusic!!.artist,
                                        SoulSearchingContext.orientation
                                    )
                                    else "",
                                    color = subTextColor,
                                    fontSize = 15.sp,
                                    maxLines = 1,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .composed {
                                            clickableArtistModifier
                                        },
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = " | ",
                                    color = subTextColor,
                                    fontSize = 15.sp,
                                )
                                Text(
                                    text = if (state.currentMusic != null) formatTextForEllipsis(
                                        state.currentMusic!!.album,
                                        SoulSearchingContext.orientation
                                    ) else "",
                                    color = subTextColor,
                                    fontSize = 15.sp,
                                    maxLines = 1,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .composed {
                                            clickableAlbumModifier
                                        },
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        Spacer(
                            modifier = Modifier.size(UiConstants.ImageSize.medium)
                        )
                    }

                    when (SoulSearchingContext.orientation) {
                        ScreenOrientation.HORIZONTAL -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.End
                            ) {
                                ExpandedPlayButtonsComposable(
                                    widthFraction = 0.45f,
                                    paddingBottom = 0.dp,
                                    mainColor = textColor,
                                    sliderInactiveBarColor = contentColor,
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

                        else -> {
                            ExpandedPlayButtonsComposable(
                                mainColor = textColor,
                                sliderInactiveBarColor = contentColor,
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

                Row(
                    modifier = Modifier
                        .height(imageSize + UiConstants.Spacing.small)
                        .fillMaxWidth()
                        .padding(
                            start = imageSize + UiConstants.Spacing.large,
                            end = UiConstants.Spacing.small
                        )
                        .alpha((playerViewManager.offset / maxHeight).coerceIn(0.0F, 1.0F)),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (state.currentMusic != null) state.currentMusic!!.name else "",
                            color = textColor,
                            maxLines = 1,
                            textAlign = TextAlign.Start,
                            fontSize = 15.sp,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (state.currentMusic != null) state.currentMusic!!.artist else "",
                            color = textColor,
                            fontSize = 12.sp,
                            maxLines = 1,
                            textAlign = TextAlign.Start,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    MinimisedPlayButtonsComposable(
                        playerViewDraggableState = playerViewManager.playerDraggableState,
                        isPlaying = state.isPlaying
                    )
                }
            }

            PlayerPanelView(
                maxHeight = maxHeight,
                musicListDraggableState = playerMusicListViewManager.musicListDraggableState,
                playerState = state,
                onSelectedMusic = playerViewModel::showMusicBottomSheet,
                coverList = coverList,
                onRetrieveLyrics = {
                    playerViewModel.onEvent(
                        PlayerEvent.GetLyrics
                    )
                },
                secondaryColor = contentColor,
                primaryColor = backgroundColor,
                contentColor = textColor,
                subTextColor = subTextColor
            )
        }
    }
}

/**
 * Retrieve a list containing the current song and its around songs (previous and next).
 * If no songs are played, return a list containing null. If the played list contains only
 * the current song, it will return a list with only the current song.
 */
private fun getAroundSongs(
    playbackManager: PlaybackManager
): List<Music?> {
    val currentSongIndex = playbackManager.currentMusicIndex

    if (currentSongIndex == -1) return listOf(null)

    if (playbackManager.playedList.size == 1) return listOf(
        playbackManager.currentMusic
    )

    return listOf(
        playbackManager.getPreviousMusic(currentSongIndex),
        playbackManager.currentMusic,
        playbackManager.getNextMusic(currentSongIndex)
    )
}

private fun formatTextForEllipsis(text: String, orientation: ScreenOrientation): String {
    if (orientation == ScreenOrientation.HORIZONTAL) {
        return text
    }
    return if (text.length > 16) {
        "${text.subSequence(0, 16)}…"
    } else {
        text
    }
}