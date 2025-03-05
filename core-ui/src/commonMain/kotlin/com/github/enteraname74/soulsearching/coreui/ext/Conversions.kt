package com.github.enteraname74.soulsearching.coreui.ext

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun Int.toDp(): Dp = with(LocalDensity.current) {
    this@toDp.toDp()
}

@Composable
fun Float.toDp(): Dp = with(LocalDensity.current) {
    this@toDp.toDp()
}

@Composable
fun Dp.toPx(): Float = with(LocalDensity.current) {
    this@toPx.toPx()
}