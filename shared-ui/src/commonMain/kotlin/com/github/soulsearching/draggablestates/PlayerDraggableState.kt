package com.github.soulsearching.draggablestates

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.ui.unit.Density
import com.github.soulsearching.types.BottomSheetStates

/**
 * State used to control the player view draggable actions.
 */
@OptIn(ExperimentalFoundationApi::class)
class PlayerDraggableState(
    val maxHeight: Float,
    density: Density,
    initialValue: BottomSheetStates = BottomSheetStates.COLLAPSED
) : DraggableState(
    density = density,
    initialValue = initialValue,
    anchors = DraggableAnchors {
        BottomSheetStates.MINIMISED at (maxHeight - 200f)
        BottomSheetStates.COLLAPSED at maxHeight
        BottomSheetStates.EXPANDED at 0f
    }
)