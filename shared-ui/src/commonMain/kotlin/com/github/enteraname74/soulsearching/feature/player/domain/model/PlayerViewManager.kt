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
@Suppress("Deprecation")
@OptIn(ExperimentalMaterialApi::class)
class PlayerViewManager {

    val playerDraggableState: SwipeableState<BottomSheetStates> =
        SwipeableState(initialValue = BottomSheetStates.COLLAPSED)

    private val _state: MutableStateFlow<BottomSheetStates> = MutableStateFlow(BottomSheetStates.COLLAPSED)
    val state = _state.asStateFlow()

    val currentValue: BottomSheetStates
        get() = playerDraggableState.currentValue
    val isAnimationRunning: Boolean
        get() = playerDraggableState.isAnimationRunning
    val offset: Float
        get() = playerDraggableState.offset.value

    suspend fun animateTo(newState: BottomSheetStates) {
        playerDraggableState.animateTo(
            targetValue = newState,
            anim = tween(UiConstants.AnimationDuration.normal),
        )
    }

    fun updateState(newState: BottomSheetStates) {
        _state.value = newState
    }
}