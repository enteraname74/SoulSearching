package com.github.soulsearching.classes.savers

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.unit.Density
import com.github.soulsearching.classes.draggablestates.SearchDraggableState
import com.github.soulsearching.classes.types.BottomSheetStates

/**
 * Saver used to save the state of a SearchDraggableState between recompositions.
 */
class SearchDraggableStateSaver(
    private val maxHeight: Float,
    private val density: Density
): Saver<SearchDraggableState, BottomSheetStates> {
    override fun restore(value: BottomSheetStates): SearchDraggableState {
        return SearchDraggableState(
            maxHeight = maxHeight,
            density = density,
            initialValue = value
        )
    }

    @OptIn(ExperimentalFoundationApi::class)
    override fun SaverScope.save(value: SearchDraggableState): BottomSheetStates {
        return value.state.currentValue
    }
}