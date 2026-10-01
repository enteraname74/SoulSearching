package com.github.enteraname74.soulsearching.ext

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.swipeable
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.swipeableview.SwipeableViewManager

@OptIn(ExperimentalMaterialApi::class)
fun Modifier.swipeableView(
    swipeableViewManager: SwipeableViewManager,
    anchors: Map<Float, BottomSheetStates>,
): Modifier =
    this
        .swipeable(
            state = swipeableViewManager.draggableState,
            orientation = Orientation.Vertical,
            anchors = anchors,
        )