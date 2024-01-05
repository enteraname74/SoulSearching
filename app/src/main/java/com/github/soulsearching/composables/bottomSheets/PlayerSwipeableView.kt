package com.github.soulsearching.composables.bottomSheets


import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.github.soulsearching.Constants
import com.github.soulsearching.classes.ColorPaletteUtils
import com.github.soulsearching.classes.PlayerUtils
import com.github.soulsearching.classes.SettingsUtils
import com.github.soulsearching.classes.draggablestates.PlayerDraggableState
import com.github.soulsearching.classes.draggablestates.PlayerMusicListDraggableState
import com.github.soulsearching.classes.enumsAndTypes.BottomSheetStates
import com.github.soulsearching.classes.enumsAndTypes.MusicBottomSheetState
import com.github.soulsearching.composables.AppImage
import com.github.soulsearching.composables.bottomSheets.music.MusicBottomSheetEvents
import com.github.soulsearching.composables.playButtons.ExpandedPlayButtonsComposable
import com.github.soulsearching.composables.playButtons.MinimisedPlayButtonsComposable
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.service.PlayerService
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.ui.theme.DynamicColor
import com.github.soulsearching.viewModels.PlayerMusicListViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.reflect.KSuspendFunction1

@SuppressLint("UnnecessaryComposedModifier")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayerDraggableView(
    maxHeight: Float,
    draggableState: PlayerDraggableState,
    playerMusicListViewModel: PlayerMusicListViewModel,
    retrieveCoverMethod: (UUID?) -> Bitmap?,
    onMusicEvent: (MusicEvent) -> Unit,
    musicListDraggableState: PlayerMusicListDraggableState,
    isMusicInFavoriteMethod: KSuspendFunction1<UUID, Boolean>,
    navigateToAlbum: (String) -> Unit,
    navigateToArtist: (String) -> Unit,
    retrieveArtistIdMethod: (UUID) -> UUID?,
    retrieveAlbumIdMethod: (UUID) -> UUID?,
    musicState: MusicState,
    playlistState: PlaylistState,
    onPlaylistEvent: (PlaylistEvent) -> Unit,
    navigateToModifyMusic: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var isMusicInFavorite by rememberSaveable {
        mutableStateOf(false)
    }

    if (draggableState.state.currentValue == BottomSheetStates.EXPANDED) {
        PlayerUtils.playerViewModel.currentMusic?.let {
            CoroutineScope(Dispatchers.IO).launch {
                isMusicInFavorite = isMusicInFavoriteMethod(it.musicId)
            }
        }
    }

    val backgroundColor: Color by animateColorAsState(
        targetValue = when(draggableState.state.currentValue) {
            BottomSheetStates.MINIMISED, BottomSheetStates.COLLAPSED -> DynamicColor.secondary
            BottomSheetStates.EXPANDED -> {
                if (SettingsUtils.settingsViewModel.isPersonalizedDynamicPlayerThemeOn()) {
                    ColorPaletteUtils.getDynamicPrimaryColor()
                } else {
                    MaterialTheme.colorScheme.primary
                }
            }
        },
        tween(Constants.AnimationTime.normal),
        label = "BACKGROUND_COLOR_PLAYER_DRAGGABLE_VIEW"
    )
    val textColor: Color by animateColorAsState(
        targetValue = when(draggableState.state.currentValue) {
            BottomSheetStates.COLLAPSED, BottomSheetStates.MINIMISED -> DynamicColor.onPrimary
            BottomSheetStates.EXPANDED -> {
                if (SettingsUtils.settingsViewModel.isPersonalizedDynamicPlayerThemeOn()) {
                    Color.White
                } else {
                    MaterialTheme.colorScheme.onPrimary
                }
            }
        },
        tween(Constants.AnimationTime.normal),
        label = "TEXT_COLOR_COLOR_PLAYER_DRAGGABLE_VIEW"
    )

    val subTextColor: Color by animateColorAsState(
        targetValue = when(draggableState.state.currentValue) {
            BottomSheetStates.COLLAPSED, BottomSheetStates.MINIMISED -> DynamicColor.subText
            BottomSheetStates.EXPANDED -> {
                if (SettingsUtils.settingsViewModel.isPersonalizedDynamicPlayerThemeOn()) {
                    Color.LightGray
                } else {
                    MaterialTheme.colorScheme.outline
                }
            }
        },
        tween(Constants.AnimationTime.normal),
        label = "SUB_TEXT_COLOR_COLOR_PLAYER_DRAGGABLE_VIEW"
    )


    val contentColor: Color by animateColorAsState(
        targetValue = when(draggableState.state.currentValue) {
            BottomSheetStates.COLLAPSED, BottomSheetStates.MINIMISED -> DynamicColor.secondary
            BottomSheetStates.EXPANDED -> {
                if (SettingsUtils.settingsViewModel.isPersonalizedDynamicPlayerThemeOn()) {
                    ColorPaletteUtils.getDynamicSecondaryColor()
                } else {
                    MaterialTheme.colorScheme.secondary
                }
            }
        },
        tween(Constants.AnimationTime.normal),
        label = "CONTENT_COLOR_COLOR_PLAYER_DRAGGABLE_VIEW"
    )

    val systemUiController = rememberSystemUiController()
    val statusBarColor: Color by animateColorAsState(
        targetValue = when(draggableState.state.currentValue) {
            BottomSheetStates.MINIMISED, BottomSheetStates.COLLAPSED -> DynamicColor.primary
            BottomSheetStates.EXPANDED -> {
                if (SettingsUtils.settingsViewModel.isPersonalizedDynamicPlayerThemeOn()) {
                    ColorPaletteUtils.getDynamicPrimaryColor()
                } else {
                    MaterialTheme.colorScheme.primary
                }
            }
        },
        tween(Constants.AnimationTime.normal),
        label = "STATUS_BAR_COLOR_COLOR_PLAYER_DRAGGABLE_VIEW"
    )

    val navigationBarColor: Color by animateColorAsState(
        targetValue = when(draggableState.state.currentValue) {
            BottomSheetStates.COLLAPSED -> DynamicColor.primary
            BottomSheetStates.MINIMISED -> DynamicColor.secondary
            BottomSheetStates.EXPANDED -> {
                if (SettingsUtils.settingsViewModel.isPersonalizedDynamicPlayerThemeOn()) {
                    ColorPaletteUtils.getDynamicSecondaryColor()
                } else {
                    MaterialTheme.colorScheme.secondary
                }
            }
        }
        ,tween(Constants.AnimationTime.normal),
        label = "NAVIGATION_BAR_COLOR_COLOR_PLAYER_DRAGGABLE_VIEW"
    )

    val backHandlerIconsColor = if (PlayerUtils.playerViewModel.currentColorPalette == null
        || !SettingsUtils.settingsViewModel.isPersonalizedDynamicPlayerThemeOn()
    ) {
        !isSystemInDarkTheme()
    } else {
        false
    }
    systemUiController.setStatusBarColor(
        color = statusBarColor,
        darkIcons = backHandlerIconsColor
    )

    systemUiController.setNavigationBarColor(
        color = navigationBarColor,
        darkIcons = backHandlerIconsColor
    )

    BackHandler(draggableState.state.currentValue == BottomSheetStates.EXPANDED) {
        coroutineScope.launch {
            if (musicListDraggableState.state.currentValue != BottomSheetStates.COLLAPSED) {
                musicListDraggableState.animateTo(BottomSheetStates.COLLAPSED)
            }
            draggableState.animateTo(BottomSheetStates.MINIMISED)
        }
    }

    if (draggableState.state.currentValue == BottomSheetStates.COLLAPSED
        && PlayerUtils.playerViewModel.isServiceLaunched
        && !draggableState.state.isAnimationRunning
    ) {
        PlayerService.stopMusic(context)
        playerMusicListViewModel.resetPlayerMusicList()
    }

    val orientation = LocalConfiguration.current.orientation

    val alphaTransition = when (orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            if ((1.0 / (abs(draggableState.state.offset) / 70)).toFloat() > 0.1) {
                (1.0 / (abs(draggableState.state.offset) / 70)).toFloat().coerceAtMost(1.0F)
            } else {
                0.0F
            }
        }
        else -> {
            if ((1.0 / (abs(
                    max(
                        draggableState.state.offset.roundToInt(),
                        0
                    )
                ) / 100)).toFloat() > 0.1
            ) {
                (1.0 / (abs(max(draggableState.state.offset.roundToInt(), 0)) / 100)).toFloat()
                    .coerceAtMost(1.0F)
            } else {
                0.0F
            }
        }
    }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    x = 0,
                    y = max(draggableState.state.offset.roundToInt(), 0)
                )
            }
            .anchoredDraggable(
                state = draggableState.state,
                orientation = Orientation.Vertical
            )
    ) {
        val mainBoxClickableModifier =
            if (draggableState.state.currentValue == BottomSheetStates.MINIMISED) {
                Modifier.clickable {
                    coroutineScope.launch {
                        draggableState.animateTo(BottomSheetStates.EXPANDED)
                    }
                }
            } else {
                if (musicListDraggableState.state.currentValue == BottomSheetStates.EXPANDED) {
                    Modifier.clickable {
                        coroutineScope.launch {
                            musicListDraggableState.animateTo(BottomSheetStates.COLLAPSED)
                        }
                    }
                } else {
                    Modifier
                }
            }

        MusicBottomSheetEvents(
            musicBottomSheetState = MusicBottomSheetState.PLAYER,
            musicState = musicState,
            playlistState = playlistState,
            onMusicEvent = onMusicEvent,
            onPlaylistsEvent = onPlaylistEvent,
            navigateToModifyMusic = { path ->
                coroutineScope.launch {
                    draggableState.animateTo(BottomSheetStates.MINIMISED)
                }.invokeOnCompletion {
                    navigateToModifyMusic(path)
                }
            },
            playerMusicListViewModel = playerMusicListViewModel,
            playerDraggableState = draggableState,
            primaryColor = backgroundColor,
            secondaryColor = navigationBarColor,
            onSecondaryColor = textColor,
            onPrimaryColor = textColor
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
                    when (orientation) {
                        Configuration.ORIENTATION_LANDSCAPE -> max(
                            (((maxWidth * 1.5) / 100) - (max(
                                draggableState.state.offset.roundToInt(),
                                0
                            ) / 15)).roundToInt().dp,
                            Constants.Spacing.small
                        )
                        else -> max(
                            (((maxWidth * 3.5) / 100) - (max(
                                draggableState.state.offset.roundToInt(),
                                0
                            ) / 40)).roundToInt().dp,
                            Constants.Spacing.small
                        )
                    }

                val imagePaddingTop =
                    when (orientation) {
                        Configuration.ORIENTATION_LANDSCAPE -> max(
                            (((maxHeight * 7) / 100) - (max(
                                draggableState.state.offset.roundToInt(),
                                0
                            ) / 5)).roundToInt().dp,
                            Constants.Spacing.small
                        )
                        else -> max(
                            (((maxHeight * 5) / 100) - (max(
                                draggableState.state.offset.roundToInt(),
                                0
                            ) / 15)).roundToInt().dp,
                            Constants.Spacing.small
                        )
                    }


                val imageSize =
                    when (orientation) {
                        Configuration.ORIENTATION_LANDSCAPE -> max(
                            (((maxWidth * 10) / 100) - (max(
                                draggableState.state.offset.roundToInt(),
                                0
                            ) / 2)).dp,
                            55.dp
                        )
                        else -> max(
                            (((maxWidth * 30) / 100) - (max(
                                draggableState.state.offset.roundToInt(),
                                0
                            ) / 7)).dp,
                            55.dp
                        )
                    }

                val imageModifier = if (draggableState.state.currentValue == BottomSheetStates.EXPANDED) {
                    Modifier.combinedClickable(
                        onLongClick = {
                            PlayerUtils.playerViewModel.currentMusic?.let {currentMusic ->
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
                            retrieveCoverMethod(PlayerUtils.playerViewModel.currentMusic?.coverId),
                        size = imageSize,
                        roundedPercent = (draggableState.state.offset / 100).roundToInt()
                            .coerceIn(3, 10)
                    )
                }

                val backImageClickableModifier =
                    if (draggableState.state.currentValue != BottomSheetStates.EXPANDED) {
                        Modifier
                    } else {
                        Modifier.clickable {
                            coroutineScope.launch {
                                if (musicListDraggableState.state.currentValue != BottomSheetStates.COLLAPSED) {
                                    musicListDraggableState.animateTo(BottomSheetStates.COLLAPSED)
                                }
                                draggableState.animateTo(BottomSheetStates.MINIMISED)
                            }
                        }
                    }

                val showMusicListModifier =
                    if (draggableState.state.currentValue != BottomSheetStates.EXPANDED) {
                        Modifier
                    } else {
                        Modifier.clickable {
                            coroutineScope.launch {
                                if (musicListDraggableState.state.currentValue == BottomSheetStates.EXPANDED) {
                                    musicListDraggableState.animateTo(BottomSheetStates.COLLAPSED)
                                } else {
                                    musicListDraggableState.animateTo(BottomSheetStates.EXPANDED)
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
                                text = if (PlayerUtils.playerViewModel.currentMusic != null) PlayerUtils.playerViewModel.currentMusic!!.name else "",
                                color = textColor,
                                maxLines = 1,
                                textAlign = TextAlign.Center,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .basicMarquee()
                            )

                            val clickableArtistModifier =
                                if (draggableState.state.currentValue == BottomSheetStates.EXPANDED) {
                                    Modifier.clickable {
                                        PlayerUtils.playerViewModel.currentMusic?.let {
                                            coroutineScope.launch {
                                                val artistId = withContext(Dispatchers.IO) {
                                                    retrieveArtistIdMethod(it.musicId)
                                                }
                                                artistId?.let { id ->
                                                    if (draggableState.state.currentValue == BottomSheetStates.EXPANDED) {
                                                        navigateToArtist(id.toString())

                                                        draggableState.animateTo(BottomSheetStates.MINIMISED)
                                                    }
                                                }

                                            }
                                        }
                                    }
                                } else {
                                    Modifier
                                }

                            val clickableAlbumModifier =
                                if (draggableState.state.currentValue == BottomSheetStates.EXPANDED) {
                                    Modifier.clickable {
                                        PlayerUtils.playerViewModel.currentMusic?.let {
                                            coroutineScope.launch {
                                                val albumId = withContext(Dispatchers.IO) {
                                                    retrieveAlbumIdMethod(it.musicId)
                                                }
                                                albumId?.let { id ->
                                                    if (draggableState.state.currentValue == BottomSheetStates.EXPANDED) {
                                                        navigateToAlbum(id.toString())

                                                        draggableState.animateTo(BottomSheetStates.MINIMISED)
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
                                    text = if (PlayerUtils.playerViewModel.currentMusic != null) formatTextForEllipsis(
                                        PlayerUtils.playerViewModel.currentMusic!!.artist,
                                        orientation
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
                                    text = if (PlayerUtils.playerViewModel.currentMusic != null) formatTextForEllipsis(
                                        PlayerUtils.playerViewModel.currentMusic!!.album,
                                        orientation
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
                            imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
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

                    when (orientation) {
                        Configuration.ORIENTATION_LANDSCAPE -> {
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
                                    playerMusicListViewModel = playerMusicListViewModel
                                )
                            }
                        }
                        else -> {
                            ExpandedPlayButtonsComposable(
                                mainColor = textColor,
                                sliderInactiveBarColor = contentColor,
                                onMusicEvent = onMusicEvent,
                                isMusicInFavorite = isMusicInFavorite,
                                playerMusicListViewModel = playerMusicListViewModel
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
                        .alpha((draggableState.state.offset / maxHeight).coerceIn(0.0F, 1.0F)),
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
                            text = if (PlayerUtils.playerViewModel.currentMusic != null) PlayerUtils.playerViewModel.currentMusic!!.name else "",
                            color = textColor,
                            maxLines = 1,
                            textAlign = TextAlign.Start,
                            fontSize = 15.sp,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (PlayerUtils.playerViewModel.currentMusic != null) PlayerUtils.playerViewModel.currentMusic!!.artist else "",
                            color = textColor,
                            fontSize = 12.sp,
                            maxLines = 1,
                            textAlign = TextAlign.Start,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    MinimisedPlayButtonsComposable(
                        playerViewDraggableState = draggableState
                    )
                }
            }
        }
    }
}

private fun formatTextForEllipsis(text: String, orientation: Int): String {
    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        return text
    }
    return if (text.length > 16) {
        "${text.subSequence(0, 16)}…"
    } else {
        text
    }
}