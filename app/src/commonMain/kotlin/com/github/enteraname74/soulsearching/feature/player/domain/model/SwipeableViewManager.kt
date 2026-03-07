@file:Suppress("Deprecation")

package com.github.enteraname74.soulsearching.feature.player.domain.model

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@OptIn(ExperimentalMaterialApi::class)

open class SwipeableViewManager {

    val draggableState: SwipeableState<BottomSheetStates> = SwipeableState(
        initialValue = BottomSheetStates.COLLAPSED,
    )

    private val _state: MutableStateFlow<BottomSheetStates> =
        MutableStateFlow(BottomSheetStates.COLLAPSED)
    val state = _state.asStateFlow()

    private val _targetState: MutableStateFlow<BottomSheetStates> =
        MutableStateFlow(BottomSheetStates.COLLAPSED)
    val targetState = _targetState.asStateFlow()

    private val _previousState: MutableStateFlow<BottomSheetStates?> = MutableStateFlow(null)
    val previousState = _previousState.asStateFlow()

    private val _nextState: MutableStateFlow<BottomSheetStates?> = MutableStateFlow(null)
    val nextState = _nextState.asStateFlow()

    val currentValue: BottomSheetStates
        get() = draggableState.currentValue
    val targetValue: BottomSheetStates
        get() = draggableState.targetValue
    val offset: Float
        get() = draggableState.offset.value

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

    fun minimiseIfPossible() {
        if (currentValue == BottomSheetStates.EXPANDED) {
            animateTo(newState = BottomSheetStates.MINIMISED)
        }
    }

    fun closeIfPossible() {
        if (currentValue == BottomSheetStates.EXPANDED) {
            animateTo(newState = BottomSheetStates.COLLAPSED)
        }
    }
}