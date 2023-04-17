package com.github.soulsearching.composables.bottomSheets

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import com.github.soulsearching.classes.BottomSheetStates
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TestBottomSheet(maxHeight : Float) {

    val swipeableState = rememberSwipeableState(initialValue = BottomSheetStates.EXPANDED)

    Box(
        modifier = Modifier
            .swipeable(
                state = swipeableState,
                orientation = Orientation.Vertical,
                anchors = mapOf(
                    0f to BottomSheetStates.EXPANDED,
                    maxHeight to BottomSheetStates.COLLAPSED,
                )
            )
            .offset {
                IntOffset(
                    x = 0,
                    y = swipeableState.offset.value.roundToInt()
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.secondary),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "TEST !!")
        }

    }
}