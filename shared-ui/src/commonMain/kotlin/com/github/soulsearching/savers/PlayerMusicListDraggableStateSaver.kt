package com.github.soulsearching.savers

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.unit.Density
import com.github.soulsearching.draggablestates.PlayerMusicListDraggableState
import com.github.soulsearching.types.BottomSheetStates

/**
 * Saver used to save the state of a SearchDraggableState between recompositions.
 */
class PlayerMusicListDraggableStateSaver(
    private val maxHeight: Float,
    private val density: Density
): Saver<PlayerMusicListDraggableState, BottomSheetStates> {
    override fun restore(value: BottomSheetStates): PlayerMusicListDraggableState {
        return PlayerMusicListDraggableState(
            maxHeight = maxHeight,
            density = density,
            initialValue = value
        )
    }

    @OptIn(ExperimentalFoundationApi::class)
    override fun SaverScope.save(value: PlayerMusicListDraggableState): BottomSheetStates {
        return value.state.currentValue
    }
}