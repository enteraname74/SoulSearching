package com.github.enteraname74.soulsearching.coreui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class WindowSize(val maxValue: Dp) {
    Small(600.dp),
    Medium(900.dp),
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun rememberWindowSize(): WindowSize {
    val density = LocalDensity.current
    val windowWidth: Dp = with(density) {
        LocalWindowInfo.current.containerSize.width.toDp()
    }

    return WindowSize.getCorrespondingWindowSize(windowWidth)
}