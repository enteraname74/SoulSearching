package com.github.enteraname74.soulsearching.coreui.ext

import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

fun Color.isDark(): Boolean =
    this.luminance() < 0.5

fun Color.blend(
    other: Color,
    @FloatRange(from = 0.0, to = 1.0) ratio: Float
): Color {
    val inverseRatio = 1 - ratio
    val a = this.alpha * inverseRatio + other.alpha * ratio
    val r = this.red * inverseRatio + other.red * ratio
    val g = this.green * inverseRatio + other.green * ratio
    val b = this.blue * inverseRatio + other.blue * ratio
    return Color(
        red = r,
        green = g,
        blue = b,
        alpha = a
    )
}