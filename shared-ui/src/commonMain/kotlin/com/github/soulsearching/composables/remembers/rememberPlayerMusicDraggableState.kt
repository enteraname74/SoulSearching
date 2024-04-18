//package com.github.soulsearching.composables.remembers
//
//import androidx.compose.foundation.layout.BoxWithConstraintsScope
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.ui.platform.LocalDensity
//import com.github.soulsearching.draggablestates.PlayerMusicListDraggableState
//import com.github.soulsearching.savers.PlayerMusicListDraggableStateSaver
//
///**
// * Create a PlayerMusicListDraggableState for managing the draggable state of the played music list view.
// * It also manages the state of the PlayerMusicListDraggableState between recompositions.
// */
//@Composable
//fun rememberPlayerMusicDraggableState(constraintsScope: BoxWithConstraintsScope): PlayerMusicListDraggableState {
//    val density = LocalDensity.current
//    val maxHeight = with(LocalDensity.current) {
//        constraintsScope.maxHeight.toPx()
//    }
//    return rememberSaveable(
//        saver = PlayerMusicListDraggableStateSaver(
//            maxHeight = maxHeight,
//            density = density
//        )
//    ) {
//        PlayerMusicListDraggableState(
//            maxHeight = maxHeight,
//            density = density
//        )
//    }
//}