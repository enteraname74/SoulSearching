package com.github.soulsearching.composables.bottomSheets


import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.QueueMusic
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import kotlin.math.max
import androidx.compose.ui.unit.sp
import com.github.soulsearching.Constants
import com.github.soulsearching.classes.ColorPaletteUtils
import com.github.soulsearching.classes.PlayerUtils
import com.github.soulsearching.classes.SettingsUtils
import com.github.soulsearching.classes.enumsAndTypes.BottomSheetStates
import com.github.soulsearching.composables.AppImage
import com.github.soulsearching.composables.playButtons.ExpandedPlayButtonsComposable
import com.github.soulsearching.composables.playButtons.MinimisedPlayButtonsComposable
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.service.PlayerService
import com.github.soulsearching.ui.theme.DynamicColor
import com.github.soulsearching.viewModels.PlayerMusicListViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.reflect.KSuspendFunction1

@SuppressLint("UnnecessaryComposedModifier")
@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun PlayerSwipeableView(
    maxHeight: Float,
    swipeableState: SwipeableState<BottomSheetStates>,
    playerMusicListViewModel: PlayerMusicListViewModel,
    retrieveCoverMethod: (UUID?) -> Bitmap?,
    onMusicEvent: (MusicEvent) -> Unit,
    musicListSwipeableState: SwipeableState<BottomSheetStates>,
    isMusicInFavoriteMethod: KSuspendFunction1<UUID, Boolean>,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var isMusicInFavorite by rememberSaveable {
        mutableStateOf(false)
    }

    if (swipeableState.currentValue == BottomSheetStates.EXPANDED) {
        PlayerUtils.playerViewModel.currentMusic?.let {
            CoroutineScope(Dispatchers.IO).launch {
                isMusicInFavorite = isMusicInFavoriteMethod(it.musicId)
            }
        }
    }

    val backgroundColor: Color by animateColorAsState(
        targetValue =
        if (swipeableState.currentValue == BottomSheetStates.MINIMISED) {
            DynamicColor.secondary
        } else if (
            SettingsUtils.settingsViewModel.isPersonalizedDynamicPlayerThemeOn()
            && PlayerUtils.playerViewModel.currentColorPalette != null
        ) {
            ColorPaletteUtils.getDynamicPrimaryColor()
        } else {
            DynamicColor.primary
        },
        tween(Constants.AnimationTime.normal)
    )
    val textColor: Color by animateColorAsState(
        targetValue =
        if (swipeableState.currentValue == BottomSheetStates.MINIMISED) {
            DynamicColor.onSecondary
        } else if (
            SettingsUtils.settingsViewModel.isPersonalizedDynamicPlayerThemeOn()
            && PlayerUtils.playerViewModel.currentColorPalette != null
        ) {
            Color.White
        } else {
            DynamicColor.onPrimary
        },
        tween(Constants.AnimationTime.normal)
    )

    val contentColor: Color by animateColorAsState(
        targetValue = if (swipeableState.currentValue != BottomSheetStates.MINIMISED
            && SettingsUtils.settingsViewModel.isPersonalizedDynamicPlayerThemeOn()
            && PlayerUtils.playerViewModel.currentColorPalette != null
        ) {
            ColorPaletteUtils.getDynamicSecondaryColor()
        } else {
            DynamicColor.secondary
        },
        tween(Constants.AnimationTime.normal)
    )

    val systemUiController = rememberSystemUiController()
    val statusBarColor: Color by animateColorAsState(
        targetValue =
        if (
            swipeableState.currentValue != BottomSheetStates.EXPANDED
            || PlayerUtils.playerViewModel.currentColorPalette == null
            || !SettingsUtils.settingsViewModel.isPersonalizedDynamicPlayerThemeOn()
        ) {
            DynamicColor.primary
        } else {
            ColorPaletteUtils.getDynamicPrimaryColor()
        },
        tween(Constants.AnimationTime.normal)
    )

    val navigationBarColor: Color by animateColorAsState(
        targetValue =
        if (swipeableState.currentValue == BottomSheetStates.COLLAPSED) {
            DynamicColor.primary
        } else if (swipeableState.currentValue == BottomSheetStates.MINIMISED
            || PlayerUtils.playerViewModel.currentColorPalette == null
            || !SettingsUtils.settingsViewModel.isPersonalizedDynamicPlayerThemeOn()
        ) {
            DynamicColor.secondary
        } else {
            ColorPaletteUtils.getDynamicSecondaryColor()
        },
        tween(Constants.AnimationTime.normal)
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


    BackHandler(swipeableState.currentValue == BottomSheetStates.EXPANDED) {
        coroutineScope.launch {
            if (musicListSwipeableState.currentValue != BottomSheetStates.COLLAPSED) {
                musicListSwipeableState.animateTo(
                    BottomSheetStates.COLLAPSED,
                    tween(Constants.AnimationTime.normal)
                )
            }
            swipeableState.animateTo(
                BottomSheetStates.MINIMISED,
                tween(Constants.AnimationTime.normal)
            )
        }
    }

    if (swipeableState.currentValue == BottomSheetStates.COLLAPSED
        && PlayerUtils.playerViewModel.isServiceLaunched
        && !swipeableState.isAnimationRunning
    ) {
        PlayerService.stopMusic(context)
        playerMusicListViewModel.resetPlayerMusicList()
    }

    val orientation = LocalConfiguration.current.orientation

    val alphaTransition = when (orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            if ((1.0 / (abs(swipeableState.offset.value) / 70)).toFloat() > 0.1) {
                (1.0 / (abs(swipeableState.offset.value) / 70)).toFloat().coerceAtMost(1.0F)
            } else {
                0.0F
            }
        }
        else -> {
            if ((1.0 / (abs(max(swipeableState.offset.value.roundToInt(), 0)) / 100)).toFloat() > 0.1) {
                (1.0 / (abs(max(swipeableState.offset.value.roundToInt(), 0)) / 100)).toFloat().coerceAtMost(1.0F)
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
                    y = max(swipeableState.offset.value.roundToInt(), 0)
                )
            }
            .swipeable(
                state = swipeableState,
                orientation = Orientation.Vertical,
                anchors = mapOf(
                    (maxHeight - 200f) to BottomSheetStates.MINIMISED,
                    maxHeight to BottomSheetStates.COLLAPSED,
                    0f to BottomSheetStates.EXPANDED
                )
            )
    ) {
        val mainBoxClickableModifier =
            if (swipeableState.currentValue == BottomSheetStates.MINIMISED) {
                Modifier.clickable {
                    coroutineScope.launch {
                        swipeableState.animateTo(
                            BottomSheetStates.EXPANDED,
                            tween(Constants.AnimationTime.normal)
                        )
                    }
                }
            } else {
                if (musicListSwipeableState.currentValue == BottomSheetStates.EXPANDED) {
                    Modifier.clickable {
                        coroutineScope.launch {
                            musicListSwipeableState.animateTo(
                                BottomSheetStates.COLLAPSED,
                                tween(Constants.AnimationTime.normal)
                            )
                        }
                    }
                } else {
                    Modifier
                }
            }

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
                            (((maxWidth * 1.5) / 100) - (max(swipeableState.offset.value.roundToInt(), 0) / 15)).roundToInt().dp,
                            Constants.Spacing.small
                        )
                        else -> max(
                            (((maxWidth * 3.5) / 100) - (max(swipeableState.offset.value.roundToInt(), 0) / 40)).roundToInt().dp,
                            Constants.Spacing.small
                        )
                    }

                val imagePaddingTop =
                    when (orientation) {
                        Configuration.ORIENTATION_LANDSCAPE -> max(
                            (((maxHeight * 7) / 100) - (max(swipeableState.offset.value.roundToInt(), 0) / 5)).roundToInt().dp,
                            Constants.Spacing.small
                        )
                        else -> max(
                            (((maxHeight * 5) / 100) - (max(swipeableState.offset.value.roundToInt(), 0) / 15)).roundToInt().dp,
                            Constants.Spacing.small
                        )
                    }


                val imageSize =
                    when (orientation) {
                        Configuration.ORIENTATION_LANDSCAPE -> max(
                            (((maxWidth * 10) / 100) - (max(swipeableState.offset.value.roundToInt(), 0) / 2)).dp,
                            55.dp
                        )
                        else -> max(
                            (((maxWidth * 30) / 100) - (max(swipeableState.offset.value.roundToInt(), 0) / 7)).dp,
                            55.dp
                        )
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
                        bitmap = retrieveCoverMethod(PlayerUtils.playerViewModel.currentMusic?.coverId),
                        size = imageSize,
                        roundedPercent = (swipeableState.offset.value / 100).roundToInt()
                            .coerceIn(3, 10)
                    )
                }

                val backImageClickableModifier =
                    if (swipeableState.currentValue != BottomSheetStates.EXPANDED) {
                        Modifier
                    } else {
                        Modifier.clickable {
                            coroutineScope.launch {
                                if (musicListSwipeableState.currentValue != BottomSheetStates.COLLAPSED) {
                                    musicListSwipeableState.animateTo(
                                        BottomSheetStates.COLLAPSED,
                                        tween(Constants.AnimationTime.normal)
                                    )
                                }
                                swipeableState.animateTo(
                                    BottomSheetStates.MINIMISED,
                                    tween(Constants.AnimationTime.normal)
                                )
                            }
                        }
                    }

                val showMusicListModifier =
                    if (swipeableState.currentValue != BottomSheetStates.EXPANDED) {
                        Modifier
                    } else {
                        Modifier.clickable {
                            coroutineScope.launch {
                                if (musicListSwipeableState.currentValue == BottomSheetStates.EXPANDED) {
                                    musicListSwipeableState.animateTo(
                                        BottomSheetStates.COLLAPSED,
                                        tween(Constants.AnimationTime.normal)
                                    )
                                } else {
                                    musicListSwipeableState.animateTo(
                                        BottomSheetStates.EXPANDED,
                                        tween(Constants.AnimationTime.normal)
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
                                text = if (PlayerUtils.playerViewModel.currentMusic != null) PlayerUtils.playerViewModel.currentMusic!!.name else "",
                                color = textColor,
                                maxLines = 1,
                                textAlign = TextAlign.Center,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .basicMarquee()
                            )
                            Text(
                                text = if (PlayerUtils.playerViewModel.currentMusic != null) PlayerUtils.playerViewModel.currentMusic!!.artist else "",
                                color = textColor,
                                fontSize = 15.sp,
                                maxLines = 1,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(250.dp),
                                overflow = TextOverflow.Ellipsis
                            )
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
                        .alpha((swipeableState.offset.value / maxHeight).coerceIn(0.0F, 1.0F)),
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
                        playerViewSwipeableState = swipeableState
                    )
                }
            }
        }
    }
}