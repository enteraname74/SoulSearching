package com.github.enteraname74.soulsearching.domain.savers//package com.github.soulsearching.savers
//
//import androidx.compose.foundation.ExperimentalFoundationApi
//import androidx.compose.runtime.saveable.Saver
//import androidx.compose.runtime.saveable.SaverScope
//import androidx.compose.ui.unit.Density
//import com.github.soulsearching.draggablestates.PlayerDraggableState
//import com.github.soulsearching.types.BottomSheetStates
//
///**
// * Saver used to save the state of a PlayerDraggableState between recompositions.
// */
//class PlayerDraggableStateSaver(
//    private val maxHeight: Float,
//    private val density: Density
//): Saver<PlayerDraggableState, BottomSheetStates> {
//    override fun restore(value: BottomSheetStates): PlayerDraggableState {
//        return PlayerDraggableState(
//            maxHeight = maxHeight,
//            density = density,
//            initialValue = value
//        )
//    }
//
//    @OptIn(ExperimentalFoundationApi::class)
//    override fun SaverScope.save(value: PlayerDraggableState): BottomSheetStates {
//        return value.state.currentValue
//    }
//}