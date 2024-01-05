package com.github.soulsearching.classes.draggablestates

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.github.soulsearching.Constants
import com.github.soulsearching.classes.enumsAndTypes.BottomSheetStates

/**
 * Manage the state of a Draggable element.
 */
open class DraggableState @OptIn(ExperimentalFoundationApi::class) constructor(
    val density: Density,
    val initialValue: BottomSheetStates = BottomSheetStates.COLLAPSED,
    anchors: DraggableAnchors<BottomSheetStates>
) {
    @OptIn(ExperimentalFoundationApi::class)
    private val _state = AnchoredDraggableState(
        initialValue = initialValue,
        anchors = anchors,
        positionalThreshold = { distance: Float -> distance * 0.5f },
        velocityThreshold = { with(density) { 100.dp.toPx() } },
        animationSpec = tween(Constants.AnimationTime.normal),
    )

    @OptIn(ExperimentalFoundationApi::class)
    val state
        get() = _state

    /**
     * Change the draggable state by switching to a target value at a given velocity.
     */
    @OptIn(ExperimentalFoundationApi::class)
    suspend fun animateTo(
        targetValue: BottomSheetStates,
        velocity: Float = Constants.AnimationTime.normal.toFloat()
    ) {
        this._state.animateTo(targetValue, velocity)
    }
}