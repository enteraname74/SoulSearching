package com.github.enteraname74.soulsearching.coreui.theme.color

import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

object DynamicColorThemeBuilder {
    fun buildDynamicTheme(paletteRgb: Int): SoulSearchingPalette = SoulSearchingPalette(
        primary = getDynamicColor(color = paletteRgb, ratio = PRIMARY_COLOR_RATIO),
        onPrimary = Color.White,
        secondary = getDynamicColor(color = paletteRgb, ratio = SECONDARY_COLOR_RATIO),
        onSecondary = Color.White,
        subText = Color.LightGray,
    )

    private fun getDynamicColor(
        color: Int,
        ratio: Float,
    ): Color = blendARGB(
        color1 = color,
        color2 = Color.Black.toArgb(),
        ratio = ratio,
    )

    /**
     * Blend two colors together.
     */
    private fun blendARGB(
        @ColorInt color1: Int,
        @ColorInt color2: Int,
        @FloatRange(from = 0.0, to = 1.0) ratio: Float
    ): Color {
        val firstColor = Color(color1)
        val secondColor = Color(color2)

        val inverseRatio = 1 - ratio
        val a = firstColor.alpha * inverseRatio + secondColor.alpha * ratio
        val r = firstColor.red * inverseRatio + secondColor.red * ratio
        val g = firstColor.green * inverseRatio + secondColor.green * ratio
        val b = firstColor.blue * inverseRatio + secondColor.blue * ratio
        return Color(
            red = r,
            green = g,
            blue = b,
            alpha = a
        )
    }

    private fun doesColorHasGoodContrast(
        foregroundColor: Color,
        backgroundColor: Color,
    ): Boolean = true

    private const val PRIMARY_COLOR_RATIO = 0.5f
    private const val SECONDARY_COLOR_RATIO = 0.2f
    private const val CONTRASTED_COLOR_THRESHOLD = 4.5f
}