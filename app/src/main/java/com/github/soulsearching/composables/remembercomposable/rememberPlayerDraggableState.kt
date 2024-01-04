package com.github.soulsearching.composables.remembercomposable

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalDensity
import com.github.soulsearching.classes.draggablestates.PlayerDraggableSaver
import com.github.soulsearching.classes.draggablestates.PlayerDraggableState

@Composable
fun rememberPlayerComposableState(constraintsScope: BoxWithConstraintsScope): PlayerDraggableState {
    val density = LocalDensity.current
    val maxHeight = with(LocalDensity.current) {
        constraintsScope.maxHeight.toPx()
    }
    return rememberSaveable(
        saver = PlayerDraggableSaver(
            maxHeight = maxHeight,
            density = density
        )
    ) { PlayerDraggableState(
        maxHeight = maxHeight,
        density = density
    )}
}