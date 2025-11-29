package com.github.enteraname74.soulsearching.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.IntOffset
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay
import com.github.enteraname74.soulsearching.coreui.UiConstants

object NavigationAnimations {
    private val tweenSpec = tween<IntOffset>(
        durationMillis = UiConstants.AnimationDuration.normal,
        delayMillis = UiConstants.AnimationDuration.short,
    )
    private val horizontalTransitionSpec: (AnimatedContentTransitionScope<Scene<*>>.() -> ContentTransform) = {
        slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tweenSpec,
        ) togetherWith
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tweenSpec,
                )
    }
    private val horizontalPopTransitionSpec: (AnimatedContentTransitionScope<Scene<*>>.() -> ContentTransform) = {
        slideInHorizontally(
            initialOffsetX = { -it },
            animationSpec = tweenSpec,
        ) togetherWith
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tweenSpec,
                )
    }

    val horizontal =
        NavDisplay.transitionSpec { horizontalTransitionSpec() } +
                NavDisplay.popTransitionSpec { horizontalPopTransitionSpec() } +
                NavDisplay.predictivePopTransitionSpec { horizontalPopTransitionSpec() }
}