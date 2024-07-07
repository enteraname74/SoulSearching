package com.github.enteraname74.soulsearching.feature.player.presentation


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.swipeable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
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
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.SoulSearchingContext
import com.github.enteraname74.soulsearching.coreui.theme.color.ColorThemeManager
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.image.SoulImage
import com.github.soulsearching.composables.SoulSearchingBackHandler
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.MusicBottomSheetEvents
import com.github.enteraname74.soulsearching.domain.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.domain.model.types.MusicBottomSheetState
import com.github.enteraname74.soulsearching.coreui.ScreenOrientation
import com.github.enteraname74.soulsearching.coreui.utils.ColorPaletteUtils
import com.github.enteraname74.soulsearching.domain.viewmodel.PlayerViewModel
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerEvent
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.enteraname74.soulsearching.feature.player.presentation.composable.ExpandedPlayButtonsComposable
import com.github.enteraname74.soulsearching.feature.player.presentation.composable.MinimisedPlayButtonsComposable
import com.github.enteraname74.soulsearching.feature.playerpanel.PlayerPanelView
import com.github.enteraname74.soulsearching.feature.settings.domain.ViewSettingsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

@Suppress("Deprecation")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun PlayerDraggableView(
    maxHeight: Float,
    draggableState: SwipeableState<BottomSheetStates>,
    retrieveCoverMethod: (UUID?) -> ImageBitmap?,
    musicListDraggableState: SwipeableState<BottomSheetStates>,
    navigateToAlbum: (String) -> Unit,
    navigateToArtist: (String) -> Unit,
    retrieveArtistIdMethod: (UUID) -> UUID?,
    retrieveAlbumIdMethod: (UUID) -> UUID?,
    navigateToModifyMusic: (String) -> Unit,
    playerViewModel: PlayerViewModel,
    coverList: ArrayList<ImageCover>,
    playbackManager: PlaybackManager = injectElement(),
    colorThemeManager: ColorThemeManager = injectElement(),
    viewSettingsManager: ViewSettingsManager = injectElement()
) {
    val coroutineScope = rememberCoroutineScope()
    val state by playerViewModel.handler.state.collectAsState()

    val backgroundColor: Color by animateColorAsState(
        targetValue = when (draggableState.currentValue) {
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
        targetValue = when (draggableState.currentValue) {
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
        targetValue = when (draggableState.currentValue) {
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
        targetValue = when (draggableState.currentValue) {
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
        targetValue = when (draggableState.currentValue) {
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
        targetValue = when (draggableState.currentValue) {
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

    var selectedMusicId by rememberSaveable {
        mutableStateOf<UUID?>(null)
    }

    SoulSearchingBackHandler(draggableState.currentValue == BottomSheetStates.EXPANDED) {
        coroutineScope.launch {
            if (musicListDraggableState.currentValue != BottomSheetStates.COLLAPSED) {
                musicListDraggableState.animateTo(
                    BottomSheetStates.COLLAPSED,
                    tween(UiConstants.AnimationDuration.normal)
                )
            }
            draggableState.animateTo(
                BottomSheetStates.MINIMISED,
                tween(UiConstants.AnimationDuration.normal)
            )
        }
    }

    // If no music is been played, and the player view is still shown, we need to hide it.
    if (state.playedList.isEmpty() &&
        draggableState.currentValue != BottomSheetStates.COLLAPSED &&
        !draggableState.isAnimationRunning
    ) {
        coroutineScope.launch {
            if (state.isMusicBottomSheetShown) {
                playerViewModel.handler.onEvent(
                    PlayerEvent.SetMusicBottomSheetVisibility(
                        isShown = false
                    )
                )
            }
            if (musicListDraggableState.currentValue != BottomSheetStates.COLLAPSED) {
                musicListDraggableState.animateTo(
                    BottomSheetStates.COLLAPSED,
                    tween(UiConstants.AnimationDuration.normal)
                )
            }
            draggableState.animateTo(
                BottomSheetStates.COLLAPSED,
                tween(UiConstants.AnimationDuration.normal)
            )
        }
    }

    if (draggableState.currentValue == BottomSheetStates.COLLAPSED
        && !draggableState.isAnimationRunning
    ) {
        playbackManager.stopPlayback()
    }

    val orientation = SoulSearchingContext.orientation
    val alphaTransition =
        if (draggableState.currentValue == BottomSheetStates.MINIMISED && draggableState.offset.value == 0f) {
            0f
        } else {
            when (orientation) {
                ScreenOrientation.HORIZONTAL -> {
                    if ((1f / (abs(draggableState.offset.value) / 70)) > 0.1) {
                        (1f / (abs(draggableState.offset.value) / 70)).coerceAtMost(1f)
                    } else {
                        0f
                    }
                }

                else -> {
                    if ((1f / (abs(
                            max(
                                draggableState.offset.value.roundToInt(),
                                0
                            )
                        ) / 100)) > 0.1
                    ) {
                        (1f / (abs(max(draggableState.offset.value.roundToInt(), 0)) / 100))
                            .coerceAtMost(1f)
                    } else {
                        0f
                    }
                }
            }
        }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    x = 0,
                    y = max(draggableState.offset.value.roundToInt(), 0)
                )
            }
            .swipeable(
                state = draggableState,
                orientation = Orientation.Vertical,
                anchors = mapOf(
                    (maxHeight - 200f) to BottomSheetStates.MINIMISED,
                    maxHeight to BottomSheetStates.COLLAPSED,
                    0f to BottomSheetStates.EXPANDED
                )
            )
    ) {
        val mainBoxClickableModifier =
            if (draggableState.currentValue == BottomSheetStates.MINIMISED) {
                Modifier.clickable {
                    coroutineScope.launch {
                        draggableState.animateTo(
                            BottomSheetStates.EXPANDED,
                            tween(UiConstants.AnimationDuration.normal)
                        )
                    }
                }
            } else {
                Modifier
            }

        state.playedList.find { it.musicId == selectedMusicId }?.let { music ->
            MusicBottomSheetEvents(
                selectedMusic = music,
                playlistsWithMusics = state.playlistsWithMusics,
                navigateToModifyMusic = { path ->
                    coroutineScope.launch {
                        if (musicListDraggableState.currentValue == BottomSheetStates.EXPANDED) {
                            musicListDraggableState.animateTo(
                                BottomSheetStates.COLLAPSED,
                                tween(UiConstants.AnimationDuration.normal)
                            )
                        }
                        draggableState.animateTo(
                            BottomSheetStates.MINIMISED,
                            tween(UiConstants.AnimationDuration.normal)
                        )
                    }.invokeOnCompletion {
                        navigateToModifyMusic(path)
                    }
                },
                musicBottomSheetState = MusicBottomSheetState.PLAYER,
                playerDraggableState = draggableState,
                isDeleteMusicDialogShown = state.isDeleteMusicDialogShown,
                isBottomSheetShown = state.isMusicBottomSheetShown,
                isAddToPlaylistBottomSheetShown = state.isAddToPlaylistBottomSheetShown,
                onDismiss = {
                    playerViewModel.handler.onEvent(
                        PlayerEvent.SetMusicBottomSheetVisibility(
                            isShown = false
                        )
                    )
                },
                onSetDeleteMusicDialogVisibility = { isShown ->
                    playerViewModel.handler.onEvent(
                        PlayerEvent.SetDeleteMusicDialogVisibility(
                            isShown = isShown
                        )
                    )
                },
                onSetAddToPlaylistBottomSheetVisibility = { isShown ->
                    playerViewModel.handler.onEvent(
                        PlayerEvent.SetAddToPlaylistBottomSheetVisibility(
                            isShown = isShown
                        )
                    )
                },
                onDeleteMusic = {
                    playerViewModel.handler.onEvent(
                        PlayerEvent.DeleteMusic(musicId = music.musicId)
                    )
                },
                onToggleQuickAccessState = {
                    playerViewModel.handler.onEvent(
                        PlayerEvent.ToggleQuickAccessState(musicId = music.musicId)
                    )
                },
                onAddMusicToSelectedPlaylists = { selectedPlaylistsIds ->
                    playerViewModel.handler.onEvent(
                        PlayerEvent.AddMusicToPlaylists(
                            musicId = music.musicId,
                            selectedPlaylistsIds = selectedPlaylistsIds
                        )
                    )
                },
                secondaryColor = navigationBarColor,
                onSecondaryColor = textColor,
                retrieveCoverMethod = retrieveCoverMethod
            )
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
                            draggableState.offset.value.roundToInt(),
                            0
                        ) / 15)).roundToInt().dp,
                        UiConstants.Spacing.small
                    )

                    else -> max(
                        (((maxWidth * 3.5) / 100) - (max(
                            draggableState.offset.value.roundToInt(),
                            0
                        ) / 40)).roundToInt().dp,
                        UiConstants.Spacing.small
                    )
                }

            val imagePaddingTop =
                when (SoulSearchingContext.orientation) {
                    ScreenOrientation.HORIZONTAL -> max(
                        (((maxHeight * 7) / 100) - (max(
                            draggableState.offset.value.roundToInt(),
                            0
                        ) / 5)).roundToInt().dp,
                        UiConstants.Spacing.small
                    )

                    else -> max(
                        (((maxHeight * 5) / 100) - (max(
                            draggableState.offset.value.roundToInt(),
                            0
                        ) / 15)).roundToInt().dp,
                        UiConstants.Spacing.small
                    )
                }


            val imageSize =
                when (SoulSearchingContext.orientation) {
                    ScreenOrientation.HORIZONTAL -> max(
                        (((maxWidth * 10) / 100) - (max(
                            draggableState.offset.value.roundToInt(),
                            0
                        ) / 2)).dp,
                        55.dp
                    )

                    else -> max(
                        (((maxWidth * 29) / 100) - (max(
                            draggableState.offset.value.roundToInt(),
                            0
                        ) / 7)).dp,
                        55.dp
                    )
                }

            val imageModifier = if (draggableState.currentValue == BottomSheetStates.EXPANDED) {
                Modifier.combinedClickable(
                    onLongClick = {
                        if (state.currentMusic != null) {
                            coroutineScope.launch {
                                selectedMusicId = state.currentMusic?.musicId
                                playerViewModel.handler.onEvent(
                                    PlayerEvent.SetMusicBottomSheetVisibility(isShown = true)
                                )
                            }
                        }
                    },
                    onClick = { }
                )
            } else {
                Modifier
            }

            val backImageClickableModifier =
                if (draggableState.currentValue != BottomSheetStates.EXPANDED) {
                    Modifier
                } else {
                    Modifier.clickable {
                        coroutineScope.launch {
                            if (musicListDraggableState.currentValue != BottomSheetStates.COLLAPSED) {
                                musicListDraggableState.animateTo(
                                    BottomSheetStates.COLLAPSED,
                                    tween(UiConstants.AnimationDuration.normal)
                                )
                            }
                            draggableState.animateTo(
                                BottomSheetStates.MINIMISED,
                                tween(UiConstants.AnimationDuration.normal)
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
                        && draggableState.currentValue == BottomSheetStates.EXPANDED
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
                                roundedPercent = (draggableState.offset.value / 100).roundToInt()
                                    .coerceIn(3, 10)
                            )
                        }
                    } else {
                        SoulImage(
                            modifier = imageModifier,
                            bitmap =
                            retrieveCoverMethod(playbackManager.currentMusic?.coverId),
                            size = imageSize,
                            roundedPercent = (draggableState.offset.value / 100).roundToInt()
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
                                if (draggableState.currentValue == BottomSheetStates.EXPANDED) {
                                    Modifier.clickable {
                                        playbackManager.currentMusic?.let {
                                            coroutineScope.launch {
                                                val artistId = withContext(Dispatchers.IO) {
                                                    retrieveArtistIdMethod(it.musicId)
                                                }
                                                artistId?.let { id ->
                                                    if (draggableState.currentValue == BottomSheetStates.EXPANDED) {
                                                        navigateToArtist(id.toString())

                                                        draggableState.animateTo(
                                                            BottomSheetStates.MINIMISED,
                                                            tween(UiConstants.AnimationDuration.normal)
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
                                if (draggableState.currentValue == BottomSheetStates.EXPANDED) {
                                    Modifier.clickable {
                                        playbackManager.currentMusic?.let {
                                            coroutineScope.launch {
                                                val albumId = withContext(Dispatchers.IO) {
                                                    retrieveAlbumIdMethod(it.musicId)
                                                }
                                                albumId?.let { id ->
                                                    if (draggableState.currentValue == BottomSheetStates.EXPANDED) {
                                                        navigateToAlbum(id.toString())

                                                        draggableState.animateTo(
                                                            BottomSheetStates.MINIMISED,
                                                            tween(UiConstants.AnimationDuration.normal)
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
                                        playerViewModel.handler.onEvent(
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
                                    playerViewModel.handler.onEvent(
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
                        .alpha((draggableState.offset.value / maxHeight).coerceIn(0.0F, 1.0F)),
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
                        playerViewDraggableState = draggableState,
                        isPlaying = state.isPlaying
                    )
                }
            }

            PlayerPanelView(
                maxHeight = maxHeight,
                musicListDraggableState = musicListDraggableState,
                playerState = state,
                onSelectedMusic = { selectedMusic ->
                    coroutineScope.launch {
                        selectedMusicId = selectedMusic.musicId
                        playerViewModel.handler.onEvent(
                            PlayerEvent.SetMusicBottomSheetVisibility(
                                isShown = true
                            )
                        )
                    }
                },
                coverList = coverList,
                onRetrieveLyrics = {
                    playerViewModel.handler.onEvent(
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