package com.github.soulsearching.classes.draggablestates

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.ui.unit.Density
import com.github.soulsearching.classes.types.BottomSheetStates

/**
 * State used to control the search view draggable actions.
 */
@OptIn(ExperimentalFoundationApi::class)
class SearchDraggableState(
    val maxHeight: Float,
    density: Density,
    initialValue: BottomSheetStates = BottomSheetStates.COLLAPSED
): DraggableState(
    density = density,
    initialValue = initialValue,
    anchors = DraggableAnchors {
        BottomSheetStates.EXPANDED at 0f
        BottomSheetStates.COLLAPSED at maxHeight
    }
)