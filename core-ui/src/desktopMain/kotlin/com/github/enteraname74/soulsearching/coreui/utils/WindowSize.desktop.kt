package com.github.enteraname74.soulsearching.coreui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun rememberWindowSize(): WindowSize {
    val density = LocalDensity.current
    val windowWidth: Dp = with(density) {
        LocalWindowInfo.current.containerSize.width.toDp()
    }

    return WindowSize.getCorrespondingWindowSize(windowWidth)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun rememberWindowHeight(): Float = LocalWindowInfo.current.containerSize.height.toFloat()

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun rememberWindowWidth(): Float = LocalWindowInfo.current.containerSize.width.toFloat()


@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun rememberWindowWidthDp(): Dp = with(LocalDensity.current) {
    LocalWindowInfo.current.containerSize.width.toDp()
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun rememberWindowHeightDp(): Dp = with(LocalDensity.current) {
    LocalWindowInfo.current.containerSize.height.toDp()
}