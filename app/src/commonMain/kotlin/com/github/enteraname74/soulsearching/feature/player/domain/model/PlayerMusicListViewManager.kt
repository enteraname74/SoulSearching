@file:Suppress("Deprecation")
package com.github.enteraname74.soulsearching.feature.player.domain.model

import androidx.compose.animation.core.tween
import androidx.compose.material.ExperimentalMaterialApi
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.SwipeableState
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates

/**
 * Manages the view of the player music list draggable view.
 */

@OptIn(ExperimentalMaterialApi::class)
class PlayerMusicListViewManager {
    val musicListDraggableState: SwipeableState<BottomSheetStates> =
        SwipeableState(initialValue = BottomSheetStates.COLLAPSED)

    val currentValue: BottomSheetStates
        get() = musicListDraggableState.currentValue

    val offset: Float
        get() = musicListDraggableState.offset.value

    suspend fun animateTo(newState: BottomSheetStates) {
        try {
            musicListDraggableState.animateTo(
                targetValue = newState,
                anim = tween(UiConstants.AnimationDuration.normal),
            )
        } catch (_: Exception) {}
    }
}