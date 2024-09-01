package com.github.enteraname74.soulsearching.coreui.theme.color

import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import kotlin.math.max
import kotlin.math.min

object DynamicColorThemeBuilder {
    fun buildDynamicTheme(paletteRgb: Int): SoulSearchingPalette {
        val primary = getDynamicColor(
            color = Color(paletteRgb),
            ratio = PRIMARY_COLOR_RATIO
        )

        val secondary = getDynamicColor(
            color = Color(paletteRgb),
            ratio = SECONDARY_COLOR_RATIO
        )

        return SoulSearchingPalette(
            primary = primary,
            onPrimary = getTextColor(backgroundColor = primary),
            secondary = secondary,
            onSecondary = getTextColor(backgroundColor = secondary),
            subSecondaryText = getTextColor(
                backgroundColor = secondary,
                baseLight = Color.LightGray,
                baseDark = Color.DarkGray,
            ),
            subPrimaryText = getTextColor(
                backgroundColor = primary,
                baseLight = Color.LightGray,
                baseDark = Color.DarkGray,
            )
        )
    }

    private fun getDynamicColor(
        color: Color,
        ratio: Float,
    ): Color = blendARGB(
        color1 = color,
        color2 = Color.Black,
        ratio = ratio,
    )

    /**
     * Blend two colors together.
     */
    private fun blendARGB(
        color1: Color,
        color2: Color,
        @FloatRange(from = 0.0, to = 1.0) ratio: Float
    ): Color {

        val inverseRatio = 1 - ratio
        val a = color1.alpha * inverseRatio + color2.alpha * ratio
        val r = color1.red * inverseRatio + color2.red * ratio
        val g = color1.green * inverseRatio + color2.green * ratio
        val b = color1.blue * inverseRatio + color2.blue * ratio
        return Color(
            red = r,
            green = g,
            blue = b,
            alpha = a
        )
    }

    private fun getTextColor(
        backgroundColor: Color,
        baseLight: Color = Color.White,
        baseDark: Color = Color.Black,
    ): Color {

        val lightColor = blendARGB(
            color1 = baseLight,
            color2 = backgroundColor,
            ratio = 0.2f
        )

        val darkColor = blendARGB(
            color1 = baseDark,
            color2 = backgroundColor,
            ratio = 0.4f,
        )

        val whiteContrast = getContrast(
            foregroundColor = lightColor,
            backgroundColor = backgroundColor,
        )
        val darkContrast = getContrast(
            foregroundColor = darkColor,
            backgroundColor = backgroundColor,
        )

        return if (whiteContrast >= CONTRASTED_COLOR_THRESHOLD || whiteContrast >= darkContrast) {
            lightColor
        } else {
            darkColor
        }
    }

    private fun getContrast(
        foregroundColor: Color,
        backgroundColor: Color,
    ): Float {
        val foregroundLuminance = foregroundColor.luminance()
        val backgroundLuminance = backgroundColor.luminance()

        val brightest = max(foregroundLuminance, backgroundLuminance)
        val darkest = min(foregroundLuminance, backgroundLuminance)

        return (brightest + 0.05f) / (darkest + 0.05f)
    }

    private const val PRIMARY_COLOR_RATIO = 0.5f
    private const val SECONDARY_COLOR_RATIO = 0.2f
    private const val CONTRASTED_COLOR_THRESHOLD = 4.5f
}