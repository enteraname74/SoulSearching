@file:Suppress("Deprecation")
package com.github.enteraname74.soulsearching.feature.player.domain.model

import androidx.compose.animation.core.tween
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages the view of the player draggable view.
 */
@OptIn(ExperimentalMaterialApi::class)
class PlayerViewManager {

    val playerDraggableState: SwipeableState<BottomSheetStates> =
        SwipeableState(initialValue = BottomSheetStates.COLLAPSED)

    private val _state: MutableStateFlow<BottomSheetStates> = MutableStateFlow(BottomSheetStates.COLLAPSED)
    val state = _state.asStateFlow()

    private val _previousState: MutableStateFlow<BottomSheetStates?> = MutableStateFlow(null)
    val previousState = _previousState.asStateFlow()

    val isAnimationRunning: Boolean
        get() = playerDraggableState.isAnimationRunning

    val currentValue: BottomSheetStates
        get() = playerDraggableState.currentValue
    val offset: Float
        get() = playerDraggableState.offset.value

    suspend fun animateTo(newState: BottomSheetStates) {
        playerDraggableState.animateTo(
            targetValue = newState,
            anim = tween(UiConstants.AnimationDuration.normal),
        )
    }

    fun consumePreviousState() {
        _previousState.value = null
    }

    fun updateState(newState: BottomSheetStates) {
        _previousState.value = _state.value
        _state.value = newState
    }
}