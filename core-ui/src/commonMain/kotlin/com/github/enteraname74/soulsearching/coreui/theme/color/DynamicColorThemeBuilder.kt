package com.github.enteraname74.soulsearching.coreui.theme.color

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.github.enteraname74.soulsearching.coreui.ext.blend
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
    ): Color = color.blend(
        other = Color.Black,
        ratio = ratio,
    )

    private fun getTextColor(
        backgroundColor: Color,
        baseLight: Color = Color.White,
        baseDark: Color = Color.Black,
    ): Color {

        val lightColor = baseLight.blend(
            other = backgroundColor,
            ratio = 0.2f
        )

        val darkColor = baseDark.blend(
            other = backgroundColor,
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