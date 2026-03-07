package com.github.enteraname74.soulsearching.feature.player.domain.model

import androidx.compose.animation.core.tween
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.github.enteraname74.soulsearching.coreui.UiConstants
import kotlinx.coroutines.launch

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
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(nextState) {
        nextState?.let {
            coroutineScope.launch {
                swipeableViewManager.draggableState.animateTo(
                    targetValue = it,
                    anim = tween(UiConstants.AnimationDuration.normal),
                )
                swipeableViewManager.consumeNextState()
            }
        }
    }
}