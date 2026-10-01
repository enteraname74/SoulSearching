package com.github.enteraname74.soulsearching.feature.swipeableview

import androidx.compose.animation.core.tween
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.enteraname74.soulsearching.coreui.UiConstants

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeableViewManagerHandler(
    swipeableViewManager: SwipeableViewManager,
) {
    LaunchedEffect(swipeableViewManager.currentValue) {
        swipeableViewManager.updateState(newState = swipeableViewManager.currentValue)
    }

    LaunchedEffect(swipeableViewManager.draggableState.targetValue) {
        swipeableViewManager.updateTargetState(newState = swipeableViewManager.draggableState.targetValue)
    }

    val nextState by swipeableViewManager.nextState.collectAsState()
    val snapState by swipeableViewManager.snapState.collectAsState()

    LaunchedEffect(snapState) {
        val requestedState = snapState ?: return@LaunchedEffect

        try {
            swipeableViewManager.draggableState.snapTo(requestedState)
        } finally {
            swipeableViewManager.consumeSnapState(requestedState)
        }
    }

    LaunchedEffect(nextState) {
        val requestedState = nextState ?: return@LaunchedEffect

        try {
            swipeableViewManager.draggableState.animateTo(
                targetValue = requestedState,
                anim = tween(UiConstants.AnimationDuration.normal),
            )
        } finally {
            swipeableViewManager.consumeNextState(requestedState)
        }
    }
}
