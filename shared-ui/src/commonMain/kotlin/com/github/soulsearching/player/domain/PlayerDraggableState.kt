package com.github.soulsearching.player.domain

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material.rememberSwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.github.soulsearching.Constants
import com.github.soulsearching.domain.model.types.BottomSheetStates
import kotlinx.coroutines.launch

/**
 * State of the player draggable view.
 */
class PlayerDraggableState {
    @OptIn(ExperimentalMaterialApi::class)
    val state: SwipeableState<BottomSheetStates> = SwipeableState(initialValue = BottomSheetStates.COLLAPSED)

    @OptIn(ExperimentalMaterialApi::class)
    val currentValue: BottomSheetStates
        @Composable
        get() = state.currentValue
}