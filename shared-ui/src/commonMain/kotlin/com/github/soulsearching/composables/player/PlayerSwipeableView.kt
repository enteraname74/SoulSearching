package com.github.soulsearching.composables.player


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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.QueueMusic
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.swipeable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.github.soulsearching.Constants
import com.github.soulsearching.SoulSearchingContext
import com.github.soulsearching.composables.AppImage
import com.github.soulsearching.composables.SoulSearchingBackHandler
import com.github.soulsearching.composables.bottomsheets.music.MusicBottomSheetEvents
import com.github.soulsearching.composables.playbuttons.ExpandedPlayButtonsComposable
import com.github.soulsearching.composables.playbuttons.MinimisedPlayButtonsComposable
import com.github.soulsearching.di.injectElement
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.model.PlaybackManager
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.theme.ColorThemeManager
import com.github.soulsearching.theme.SoulSearchingColorTheme
import com.github.soulsearching.types.BottomSheetStates
import com.github.soulsearching.types.MusicBottomSheetState
import com.github.soulsearching.types.ScreenOrientation
import com.github.soulsearching.utils.ColorPaletteUtils
import com.github.soulsearching.utils.PlayerUtils
import com.github.soulsearching.viewmodel.PlayerMusicListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.reflect.KSuspendFunction1

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun PlayerDraggableView(
    maxHeight: Float,
    draggableState: SwipeableState<BottomSheetStates>,
    playerMusicListViewModel: PlayerMusicListViewModel,
    retrieveCoverMethod: (UUID?) -> ImageBitmap?,
    onMusicEvent: (MusicEvent) -> Unit,
    musicListDraggableState: SwipeableState<BottomSheetStates>,
    isMusicInFavoriteMethod: KSuspendFunction1<UUID, Boolean>,
    navigateToAlbum: (String) -> Unit,
    navigateToArtist: (String) -> Unit,
    retrieveArtistIdMethod: (UUID) -> UUID?,
    retrieveAlbumIdMethod: (UUID) -> UUID?,
    musicState: MusicState,
    playlistState: PlaylistState,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
    navigateToModifyMusic: (String) -> Unit,
    playbackManager: PlaybackManager,
    colorThemeManager: ColorThemeManager = injectElement()
) {
    val coroutineScope = rememberCoroutineScope()
    var isMusicInFavorite by rememberSaveable {
        mutableStateOf(false)
    }

    if (draggableState.currentValue == BottomSheetStates.EXPANDED) {
        PlayerUtils.playerViewModel.handler.currentMusic?.let {
            CoroutineScope(Dispatchers.IO).launch {
                isMusicInFavorite = isMusicInFavoriteMethod(it.musicId)
            }
        }
    }

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
        tween(Constants.AnimationDuration.normal),
        label = "BACKGROUND_COLOR_PLAYER_DRAGGABLE_VIEW"
    )
    val textColor: Color by animateColorAsState(
        targetValue = when (draggableState.currentValue) {
            BottomSheetStates.COLLAPSED, BottomSheetStates.MINIMISED -> SoulSearchingColorTheme.colorScheme.onPrimary
            BottomSheetStates.EXPANDED -> {
                if (colorThemeManager.isPersonalizedDynamicPlayerThemeOn()) {
                    Color.White
                } else {
                    SoulSearchingColorTheme.defaultTheme.onPrimary
                }
            }
        },
        tween(Constants.AnimationDuration.normal),
        label = "TEXT_COLOR_COLOR_PLAYER_DRAGGABLE_VIEW"
    )

    val subTextColor: Color by animateColorAsState(
        targetValue = when (draggableState.currentValue) {
            BottomSheetStates.COLLAPSED, BottomSheetStates.MINIMISED -> SoulSearchingColorTheme.colorScheme.subText
            BottomSheetStates.EXPANDED -> {
                if (colorThemeManager.isPersonalizedDynamicPlayerThemeOn()) {
                    Color.LightGray
                } else {
                    SoulSearchingColorTheme.defaultTheme.subText
                }
            }
        },
        tween(Constants.AnimationDuration.normal),
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
                    MaterialTheme.colorScheme.secondary
                }
            }
        },
        tween(Constants.AnimationDuration.normal),
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
        tween(Constants.AnimationDuration.normal),
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
        }, tween(Constants.AnimationDuration.normal),
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

    SoulSearchingBackHandler(draggableState.currentValue == BottomSheetStates.EXPANDED) {
        coroutineScope.launch {
            if (musicListDraggableState.currentValue != BottomSheetStates.COLLAPSED) {
                musicListDraggableState.animateTo(
                    BottomSheetStates.COLLAPSED,
                    tween(Constants.AnimationDuration.normal)
                )
            }
            draggableState.animateTo(
                BottomSheetStates.MINIMISED,
                tween(Constants.AnimationDuration.normal)
            )
        }
    }

    if (draggableState.currentValue == BottomSheetStates.COLLAPSED
        && PlayerUtils.playerViewModel.handler.isServiceLaunched
        && !draggableState.isAnimationRunning
    ) {
        playbackManager.stopMusic()
        playerMusicListViewModel.handler.resetPlayerMusicList()
    }

    val orientation = SoulSearchingContext.orientation
    val alphaTransition = if (draggableState.currentValue == BottomSheetStates.MINIMISED && draggableState.offset.value == 0f) {
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
                            tween(Constants.AnimationDuration.normal)
                        )
                    }
                }
            } else {
                if (musicListDraggableState.currentValue == BottomSheetStates.EXPANDED) {
                    Modifier.clickable {
                        coroutineScope.launch {
                            musicListDraggableState.animateTo(
                                BottomSheetStates.COLLAPSED,
                                tween(Constants.AnimationDuration.normal)
                            )
                        }
                    }
                } else {
                    Modifier
                }
            }

        MusicBottomSheetEvents(
            musicState = musicState,
            playlistState = playlistState,
            onMusicEvent = onMusicEvent,
            onPlaylistsEvent = onPlaylistEvent,
            navigateToModifyMusic = { path ->
                coroutineScope.launch {
                    draggableState.animateTo(
                        BottomSheetStates.MINIMISED,
                        tween(Constants.AnimationDuration.normal)
                    )
                }.invokeOnCompletion {
                    navigateToModifyMusic(path)
                }
            },
            musicBottomSheetState = MusicBottomSheetState.PLAYER,
            playerMusicListViewModel = playerMusicListViewModel,
            playerDraggableState = draggableState,
            secondaryColor = navigationBarColor,
            onSecondaryColor = textColor
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = backgroundColor)
                .composed {
                    mainBoxClickableModifier
                },
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .padding(Constants.Spacing.small)
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
                            Constants.Spacing.small
                        )

                        else -> max(
                            (((maxWidth * 3.5) / 100) - (max(
                                draggableState.offset.value.roundToInt(),
                                0
                            ) / 40)).roundToInt().dp,
                            Constants.Spacing.small
                        )
                    }

                val imagePaddingTop =
                    when (SoulSearchingContext.orientation) {
                        ScreenOrientation.HORIZONTAL -> max(
                            (((maxHeight * 7) / 100) - (max(
                                draggableState.offset.value.roundToInt(),
                                0
                            ) / 5)).roundToInt().dp,
                            Constants.Spacing.small
                        )

                        else -> max(
                            (((maxHeight * 5) / 100) - (max(
                                draggableState.offset.value.roundToInt(),
                                0
                            ) / 15)).roundToInt().dp,
                            Constants.Spacing.small
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
                            (((maxWidth * 30) / 100) - (max(
                                draggableState.offset.value.roundToInt(),
                                0
                            ) / 7)).dp,
                            55.dp
                        )
                    }

                val imageModifier = if (draggableState.currentValue == BottomSheetStates.EXPANDED) {
                    Modifier.combinedClickable(
                        onLongClick = {
                            PlayerUtils.playerViewModel.handler.currentMusic?.let { currentMusic ->
                                coroutineScope.launch {
                                    onMusicEvent(
                                        MusicEvent.SetSelectedMusic(
                                            currentMusic
                                        )
                                    )
                                    onMusicEvent(
                                        MusicEvent.BottomSheet(
                                            isShown = true
                                        )
                                    )
                                }
                            }
                        },
                        onClick = { }
                    )
                } else {
                    Modifier
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = imagePaddingStart,
                            top = imagePaddingTop,
                            end = imagePaddingStart
                        )
                ) {
                    AppImage(
                        modifier = imageModifier,
                        bitmap =
                        retrieveCoverMethod(PlayerUtils.playerViewModel.handler.currentMusic?.coverId),
                        size = imageSize,
                        roundedPercent = (draggableState.offset.value / 100).roundToInt()
                            .coerceIn(3, 10)
                    )
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
                                        tween(Constants.AnimationDuration.normal)
                                    )
                                }
                                draggableState.animateTo(
                                    BottomSheetStates.MINIMISED,
                                    tween(Constants.AnimationDuration.normal)
                                )
                            }
                        }
                    }

                val showMusicListModifier =
                    if (draggableState.currentValue != BottomSheetStates.EXPANDED) {
                        Modifier
                    } else {
                        Modifier.clickable {
                            coroutineScope.launch {
                                if (musicListDraggableState.currentValue == BottomSheetStates.EXPANDED) {
                                    musicListDraggableState.animateTo(
                                        BottomSheetStates.COLLAPSED,
                                        tween(Constants.AnimationDuration.normal)
                                    )
                                } else {
                                    musicListDraggableState.animateTo(
                                        BottomSheetStates.EXPANDED,
                                        tween(Constants.AnimationDuration.normal)
                                    )
                                }
                            }
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Image(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = "",
                            modifier = Modifier
                                .size(Constants.ImageSize.medium)
                                .composed { backImageClickableModifier },
                            colorFilter = ColorFilter.tint(textColor),
                            alpha = alphaTransition
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(
                                    start = Constants.Spacing.small,
                                    end = Constants.Spacing.small
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = if (PlayerUtils.playerViewModel.handler.currentMusic != null) PlayerUtils.playerViewModel.handler.currentMusic!!.name else "",
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
                                        PlayerUtils.playerViewModel.handler.currentMusic?.let {
                                            coroutineScope.launch {
                                                val artistId = withContext(Dispatchers.IO) {
                                                    retrieveArtistIdMethod(it.musicId)
                                                }
                                                artistId?.let { id ->
                                                    if (draggableState.currentValue == BottomSheetStates.EXPANDED) {
                                                        navigateToArtist(id.toString())

                                                        draggableState.animateTo(
                                                            BottomSheetStates.MINIMISED,
                                                            tween(Constants.AnimationDuration.normal)
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
                                        PlayerUtils.playerViewModel.handler.currentMusic?.let {
                                            coroutineScope.launch {
                                                val albumId = withContext(Dispatchers.IO) {
                                                    retrieveAlbumIdMethod(it.musicId)
                                                }
                                                albumId?.let { id ->
                                                    if (draggableState.currentValue == BottomSheetStates.EXPANDED) {
                                                        navigateToAlbum(id.toString())

                                                        draggableState.animateTo(
                                                            BottomSheetStates.MINIMISED,
                                                            tween(Constants.AnimationDuration.normal)
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
                                    text = if (PlayerUtils.playerViewModel.handler.currentMusic != null) formatTextForEllipsis(
                                        PlayerUtils.playerViewModel.handler.currentMusic!!.artist,
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
                                    text = if (PlayerUtils.playerViewModel.handler.currentMusic != null) formatTextForEllipsis(
                                        PlayerUtils.playerViewModel.handler.currentMusic!!.album,
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
                        Image(
                            imageVector = Icons.Rounded.QueueMusic,
                            contentDescription = "",
                            modifier = Modifier
                                .size(Constants.ImageSize.medium)
                                .composed {
                                    showMusicListModifier
                                },
                            colorFilter = ColorFilter.tint(textColor),
                            alpha = alphaTransition
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
                                    onMusicEvent = onMusicEvent,
                                    isMusicInFavorite = isMusicInFavorite,
                                    playerMusicListViewModel = playerMusicListViewModel,
                                    playbackManager = playbackManager
                                )
                            }
                        }

                        else -> {
                            ExpandedPlayButtonsComposable(
                                mainColor = textColor,
                                sliderInactiveBarColor = contentColor,
                                onMusicEvent = onMusicEvent,
                                isMusicInFavorite = isMusicInFavorite,
                                playerMusicListViewModel = playerMusicListViewModel,
                                playbackManager = playbackManager
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .height(imageSize + Constants.Spacing.small)
                        .fillMaxWidth()
                        .padding(
                            start = imageSize + Constants.Spacing.large,
                            end = Constants.Spacing.small
                        )
                        .alpha((draggableState.offset.value / maxHeight).coerceIn(0.0F, 1.0F)),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(150.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (PlayerUtils.playerViewModel.handler.currentMusic != null) PlayerUtils.playerViewModel.handler.currentMusic!!.name else "",
                            color = textColor,
                            maxLines = 1,
                            textAlign = TextAlign.Start,
                            fontSize = 15.sp,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (PlayerUtils.playerViewModel.handler.currentMusic != null) PlayerUtils.playerViewModel.handler.currentMusic!!.artist else "",
                            color = textColor,
                            fontSize = 12.sp,
                            maxLines = 1,
                            textAlign = TextAlign.Start,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    MinimisedPlayButtonsComposable(
                        playerViewDraggableState = draggableState,
                        playbackManager = playbackManager
                    )
                }
            }
        }
    }
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