package com.github.soulsearching.classes.draggablestates

import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.github.soulsearching.Constants
import com.github.soulsearching.classes.enumsAndTypes.BottomSheetStates

/**
 * State used to control draggable action on the player view.
 */
class PlayerDraggableState(
    maxHeight: Float = 0f,
    density: Density = Density(0f),
    currentValue: BottomSheetStates = BottomSheetStates.COLLAPSED
) {
    @OptIn(ExperimentalFoundationApi::class)
    private val _state = AnchoredDraggableState(
        initialValue = currentValue,
        anchors = DraggableAnchors {
            BottomSheetStates.MINIMISED at (maxHeight - 200f)
            BottomSheetStates.COLLAPSED at maxHeight
            BottomSheetStates.EXPANDED at 0f
        },
        positionalThreshold = { distance: Float -> distance * 0.5f },
        velocityThreshold = { with(density) { 100.dp.toPx() } },
        animationSpec = tween(Constants.AnimationTime.normal),
    )

    @OptIn(ExperimentalFoundationApi::class)
    val state
        get() = _state
}

class PlayerDraggableSaver(
    val maxHeight: Float,
    val density: Density
) : Saver<PlayerDraggableState, BottomSheetStates> {
    override fun restore(value: BottomSheetStates): PlayerDraggableState {
        println("RESTORE WITH STATE : $value")
        return PlayerDraggableState(
            maxHeight = maxHeight,
            density = density,
            currentValue = value
        )
    }

    @OptIn(ExperimentalFoundationApi::class)
    override fun SaverScope.save(value: PlayerDraggableState): BottomSheetStates {
        println("SAVE STATE : ${value.state.currentValue}")
        return value.state.currentValue
    }
}