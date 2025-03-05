package com.github.enteraname74.soulsearching.coreui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal actual val MediumThreshold: Dp = 1200.dp

@Composable
actual fun rememberWindowSize(): WindowSize {
    LocalContext.current
    val windowWidth: Dp = LocalConfiguration.current.screenWidthDp.dp
    return WindowSize.getCorrespondingWindowSize(windowWidth)
}

@Composable
actual fun rememberWindowHeight(): Float = with(LocalDensity.current) {
    LocalConfiguration.current.screenHeightDp.dp.toPx()
}

@Composable
actual fun rememberWindowWidth(): Float = with(LocalDensity.current) {
    LocalConfiguration.current.screenWidthDp.dp.toPx()
}

@Composable
actual fun rememberWindowWidthDp(): Dp = LocalConfiguration.current.screenWidthDp.dp

@Composable
actual fun rememberWindowHeightDp(): Dp = LocalConfiguration.current.screenHeightDp.dp