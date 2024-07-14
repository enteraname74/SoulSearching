package com.github.enteraname74.soulsearching.feature.player.domain.model

import androidx.compose.animation.core.tween
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates

/**
 * Manages the view of the player music list draggable view.
 */
@Suppress("Deprecation")
@OptIn(ExperimentalMaterialApi::class)
class PlayerMusicListViewManager {
    val musicListDraggableState: SwipeableState<BottomSheetStates> =
        SwipeableState(initialValue = BottomSheetStates.COLLAPSED)

    val currentValue: BottomSheetStates = musicListDraggableState.currentValue

    suspend fun animateTo(newState: BottomSheetStates) {
        musicListDraggableState.animateTo(
            targetValue = newState,
            anim = tween(UiConstants.AnimationDuration.normal),
        )
    }
}