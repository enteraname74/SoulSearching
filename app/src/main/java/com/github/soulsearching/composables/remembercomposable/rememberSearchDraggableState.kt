package com.github.soulsearching.composables.remembercomposable

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalDensity
import com.github.soulsearching.classes.draggablestates.SearchDraggableState
import com.github.soulsearching.classes.savers.SearchDraggableStateSaver

/**
 * Create a SearchDraggableState for managing the draggable state of the search view.
 * It also manages the state of the SearchDraggableState between recompositions.
 */
@Composable
fun rememberSearchDraggableState(constraintsScope: BoxWithConstraintsScope): SearchDraggableState {
    val density = LocalDensity.current
    val maxHeight = with(LocalDensity.current) {
        constraintsScope.maxHeight.toPx()
    }
    return rememberSaveable(
        saver = SearchDraggableStateSaver(
            maxHeight = maxHeight,
            density = density
        )
    ) {
        SearchDraggableState(
            maxHeight = maxHeight,
            density = density
        )
    }
}