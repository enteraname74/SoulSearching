package com.github.soulsearching.composables.bottomSheets


import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.github.soulsearching.database.model.ImageCover
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun PlayerSwipeableView(
    maxHeight: Float,
    swipeableState: SwipeableState<BottomSheetStates>,
    coverList: ArrayList<ImageCover>
) {

    val coroutineScope = rememberCoroutineScope()

    BackHandler(swipeableState.currentValue == BottomSheetStates.EXPANDED) {
        coroutineScope.launch {
            swipeableState.animateTo(BottomSheetStates.MINIMISED, tween(300))
        }
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.secondary)
                    .clickable {
                        if (swipeableState.currentValue == BottomSheetStates.MINIMISED) {
                            coroutineScope.launch {
                                swipeableState.animateTo(BottomSheetStates.EXPANDED, tween(300))
                            }
                        }
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
                        if ((((maxWidth * 5) / 100) - (swipeableState.offset.value / 40)).roundToInt().dp > Constants.Spacing.small) {
                            (((maxWidth * 5) / 100) - (swipeableState.offset.value / 40)).roundToInt().dp
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
                        if ((((maxWidth * 28) / 100) - (swipeableState.offset.value / 7).roundToInt()).dp > 55.dp) {
                            (((maxWidth * 28) / 100) - (swipeableState.offset.value / 7).roundToInt()).dp
                        } else {
                            55.dp
                        }

                    val musicTitlePaddingEnd = if ((swipeableState.offset.value / 16).roundToInt().dp > 0.dp) {
                        (swipeableState.offset.value / 16).roundToInt().dp
                    } else {
                        0.dp
                    }

                    Log.d("OFFSET", swipeableState.offset.value.toString())
                    Log.d("MAX HEIGHT", maxHeight.toString())
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
                            roundedPercent = (swipeableState.offset.value / 100).roundToInt().coerceIn(3,10)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = Constants.Spacing.medium,
                                end = musicTitlePaddingEnd
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Log.d("RATIO", (200 - (swipeableState.offset.value / 50)).toString())
                        Text(
                            text = if (PlayerUtils.playerViewModel.currentMusic != null) PlayerUtils.playerViewModel.currentMusic!!.name else "",
                            color = MaterialTheme.colorScheme.onSecondary,
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = (20 - (swipeableState.offset.value / 100)).roundToInt()
                                .coerceAtMost(20).coerceAtLeast(15).sp,
                            modifier = Modifier
                                .width((200 - (swipeableState.offset.value / 20)).roundToInt()
                                    .coerceAtMost(200).coerceAtLeast(120).dp)
                        )
                        Text(
                            text = if (PlayerUtils.playerViewModel.currentMusic != null) PlayerUtils.playerViewModel.currentMusic!!.artist else "",
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontSize = (17 - (swipeableState.offset.value / 100)).roundToInt()
                                .coerceAtMost(17).coerceAtLeast(12).sp
                        )
                    }
                }
            }
        }
    }
}