package com.github.soulsearching.composables.bottomSheets


import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.github.soulsearching.Constants
import com.github.soulsearching.classes.BottomSheetStates
import com.github.soulsearching.composables.AppImage
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TestBottomSheet(maxHeight: Float) {
    val swipeableState = rememberSwipeableState(initialValue = BottomSheetStates.EXPANDED)
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
                        if ((((maxWidth * 4) / 100) - (swipeableState.offset.value / 40)).roundToInt().dp > Constants.Spacing.small) {
                            (((maxWidth * 4) / 100) - (swipeableState.offset.value / 40)).roundToInt().dp
                        } else {
                            Constants.Spacing.small
                        }

                    val imagePaddingTop =
                        if ((((maxHeight * 5) / 100) - (swipeableState.offset.value / 15)).roundToInt().dp > Constants.Spacing.small) {
                            (((maxHeight * 5) / 100) - (swipeableState.offset.value / 15)).roundToInt().dp
                        } else {
                            Constants.Spacing.small
                        }

                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = imagePaddingStart, top = imagePaddingTop, end = imagePaddingStart)
                        .background(color = MaterialTheme.colorScheme.primary)
                    ){
                        AppImage(
                            bitmap = null, size = (
                                    if ((((maxWidth * 28) / 100) - (swipeableState.offset.value / 7).roundToInt()).dp > 55.dp) {
                                        (((maxWidth * 28) / 100) - (swipeableState.offset.value / 7).roundToInt()).dp
                                    } else {
                                        55.dp
                                    }

                                    )
                        )
                    }
                }
            }
        }
    }
}