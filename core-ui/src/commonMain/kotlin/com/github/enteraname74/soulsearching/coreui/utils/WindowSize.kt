package com.github.enteraname74.soulsearching.coreui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class WindowSize(val maxValue: Dp) {
    Small(600.dp),
    Medium(MediumThreshold),
    Large(Dp.Infinity);

    companion object {
        fun getCorrespondingWindowSize(width: Dp): WindowSize =
            when {
                width <= Small.maxValue -> Small
                width <= Medium.maxValue -> Medium
                else -> Large
            }
    }
}

internal expect val MediumThreshold: Dp

@Composable
expect fun rememberWindowSize(): WindowSize

@Composable
expect fun rememberWindowHeight(): Float

@Composable
expect fun rememberWindowWidth(): Float

@Composable
expect fun rememberWindowHeightDp(): Dp

@Composable
expect fun rememberWindowWidthDp(): Dp