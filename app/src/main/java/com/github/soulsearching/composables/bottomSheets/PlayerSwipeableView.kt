package com.github.soulsearching.composables.bottomSheets


import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.swipeable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.soulsearching.Constants
import com.github.soulsearching.classes.BottomSheetStates
import com.github.soulsearching.classes.PlayerUtils
import com.github.soulsearching.composables.AppImage
import com.github.soulsearching.composables.playButtons.ExpandedPlayButtonsComposable
import com.github.soulsearching.composables.playButtons.MinimisedPlayButtonsComposable
import com.github.soulsearching.database.model.ImageCover
import com.github.soulsearching.service.PlayerService
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@SuppressLint("UnnecessaryComposedModifier")
@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun PlayerSwipeableView(
    maxHeight: Float,
    swipeableState: SwipeableState<BottomSheetStates>,
    coverList: ArrayList<ImageCover>
) {

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    BackHandler(swipeableState.currentValue == BottomSheetStates.EXPANDED) {
        coroutineScope.launch {
            swipeableState.animateTo(BottomSheetStates.MINIMISED, tween(300))
        }
    }

    if (swipeableState.currentValue == BottomSheetStates.COLLAPSED
        && PlayerUtils.playerViewModel.isServiceLaunched
        && !swipeableState.isAnimationRunning
    ) {
        PlayerService.stopMusic(context)
    }

    Box {
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = 0,
                        y = swipeableState.offset.value.roundToInt()
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
                            swipeableState.animateTo(BottomSheetStates.EXPANDED, tween(300))
                        }
                    }
                } else {
                    Modifier
                }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.secondary)
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
                        if ((((maxWidth * 3.5) / 100) - (swipeableState.offset.value / 40)).roundToInt().dp > Constants.Spacing.small) {
                            (((maxWidth * 3.5) / 100) - (swipeableState.offset.value / 40)).roundToInt().dp
                        } else {
                            Constants.Spacing.small
                        }

                    val imagePaddingTop =
                        if ((((maxHeight * 5) / 100) - (swipeableState.offset.value / 15)).roundToInt().dp > Constants.Spacing.small) {
                            (((maxHeight * 5) / 100) - (swipeableState.offset.value / 15)).roundToInt().dp
                        } else {
                            Constants.Spacing.small
                        }

                    val imageSize =
                        if ((((maxWidth * 30) / 100) - (swipeableState.offset.value / 7).roundToInt()).dp > 55.dp) {
                            (((maxWidth * 30) / 100) - (swipeableState.offset.value / 7).roundToInt()).dp
                        } else {
                            55.dp
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
                            bitmap = coverList.find { it.coverId == PlayerUtils.playerViewModel.currentMusic?.coverId }?.cover,
                            size = imageSize,
                            roundedPercent = (swipeableState.offset.value / 100).roundToInt()
                                .coerceIn(3, 10)
                        )
                    }

                    val alphaTransition =
                        if ((1.0 / (abs(swipeableState.offset.value) / 100)).toFloat() > 0.1) {
                            (1.0 / (abs(swipeableState.offset.value) / 100)).toFloat().coerceAtMost(1.0F)
                        } else {
                            0.0F
                        }

                    val backImageClickableModifier =
                        if (swipeableState.currentValue != BottomSheetStates.EXPANDED) {
                            Modifier
                        } else {
                            Modifier.clickable {
                                coroutineScope.launch {
                                    swipeableState.animateTo(
                                        BottomSheetStates.MINIMISED,
                                        tween(300)
                                    )
                                }
                            }
                        }

                    Image(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = "",
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .size(Constants.ImageSize.medium)
                            .composed { backImageClickableModifier },
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary),
                        alpha = alphaTransition
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(alphaTransition),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = if (PlayerUtils.playerViewModel.currentMusic != null) PlayerUtils.playerViewModel.currentMusic!!.name else "",
                                color = MaterialTheme.colorScheme.onSecondary,
                                maxLines = 1,
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp,
                                modifier = Modifier
                                    .width(250.dp)
                                    .basicMarquee()
                            )
                            Text(
                                text = if (PlayerUtils.playerViewModel.currentMusic != null) PlayerUtils.playerViewModel.currentMusic!!.artist else "",
                                color = MaterialTheme.colorScheme.onSecondary,
                                fontSize = 17.sp,
                                maxLines = 1,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(250.dp),
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        ExpandedPlayButtonsComposable(modifier = Modifier.padding(bottom = 120.dp))
                    }

                    Row(
                        modifier = Modifier
                            .height(imageSize + Constants.Spacing.small)
                            .fillMaxWidth()
                            .padding(
                                start = imageSize + Constants.Spacing.large,
                                end = Constants.Spacing.small
                            )
                            .alpha((swipeableState.offset.value / maxHeight).coerceIn(0.0F,1.0F)),
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
                                color = MaterialTheme.colorScheme.onSecondary,
                                maxLines = 1,
                                textAlign = TextAlign.Start,
                                fontSize = 15.sp,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (PlayerUtils.playerViewModel.currentMusic != null) PlayerUtils.playerViewModel.currentMusic!!.artist else "",
                                color = MaterialTheme.colorScheme.onSecondary,
                                fontSize = 12.sp,
                                maxLines = 1,
                                textAlign = TextAlign.Start,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        MinimisedPlayButtonsComposable()
                    }
                }
            }
        }
    }
}