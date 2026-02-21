@file:Suppress("Deprecation")
package com.github.enteraname74.soulsearching.feature.player.domain.model

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
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

    private val _targetState: MutableStateFlow<BottomSheetStates> = MutableStateFlow(BottomSheetStates.COLLAPSED)
    val targetState = _targetState.asStateFlow()

    private val _previousState: MutableStateFlow<BottomSheetStates?> = MutableStateFlow(null)
    val previousState = _previousState.asStateFlow()
    
    private val _nextState: MutableStateFlow<BottomSheetStates?> = MutableStateFlow(null)
    val nextState = _nextState.asStateFlow()

    val isAnimationRunning: Boolean
        get() = playerDraggableState.isAnimationRunning

    val currentValue: BottomSheetStates
        get() = playerDraggableState.currentValue
    val targetValue: BottomSheetStates
        get() = playerDraggableState.targetValue
    val offset: Float
        get() = playerDraggableState.offset.value

    fun animateTo(newState: BottomSheetStates) {
        _nextState.value = newState
    }

    fun consumePreviousState() {
        _previousState.value = null
    }
    
    fun consumeNextState() {
        _nextState.value = null
    }

    fun updateState(newState: BottomSheetStates) {
        _previousState.value = _state.value
        _state.value = newState
    }

    fun updateTargetState(newState: BottomSheetStates) {
        _targetState.value = newState
    }
}