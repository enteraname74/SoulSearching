package com.github.enteraname74.soulsearching.coreui.theme.color

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.github.enteraname74.soulsearching.coreui.ext.blend
import kotlin.math.max
import kotlin.math.min

object DynamicColorThemeBuilder {
    fun buildDynamicTheme(paletteRgb: Int): SoulSearchingPalette {
        val background = getDynamicColor(
            color = Color(paletteRgb),
            ratio = BACKGROUND_COLOR_RATIO
        )

        val text = getTextColor(backgroundColor = background)
        val textSecondary = getTextColor(
            backgroundColor = background,
            baseLight = Color.LightGray,
            baseDark = Color.DarkGray,
        )
        val accent = getAccentColor(
            color = Color(paletteRgb),
            backgroundColor = background,
        )

        return SoulSearchingPalette(
            background = background,
            surfaceLow = getDynamicColor(
                color = Color(paletteRgb),
                ratio = SURFACE_LOW_COLOR_RATIO,
            ),
            surface = getDynamicColor(
                color = Color(paletteRgb),
                ratio = SURFACE_COLOR_RATIO,
            ),
            surfaceHigh = getDynamicColor(
                color = Color(paletteRgb),
                ratio = SURFACE_HIGH_COLOR_RATIO,
            ),
            outline = text.copy(alpha = OUTLINE_ALPHA),
            text = text,
            textSecondary = textSecondary,
            textTertiary = textSecondary.blend(
                other = background,
                ratio = TEXT_TERTIARY_RATIO,
            ),
            accent = accent,
            onAccent = getTextColor(backgroundColor = accent),
            danger = DYNAMIC_DANGER_COLOR,
            onDanger = background,
            warning = DYNAMIC_WARNING_COLOR,
            onWarning = background,
        )
    }

    /**
     * Lightens the given color until it is readable on the background.
     */
    private fun getAccentColor(
        color: Color,
        backgroundColor: Color,
    ): Color {
        var accent = color
        var step = 0
        while (
            getContrast(foregroundColor = accent, backgroundColor = backgroundColor) < CONTRASTED_COLOR_THRESHOLD &&
            step < MAX_ACCENT_LIGHTENING_STEPS
        ) {
            accent = accent.blend(
                other = Color.White,
                ratio = ACCENT_LIGHTENING_RATIO,
            )
            step++
        }
        return accent
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
            ratio = LIGHT_TEXT_BLEND_RATIO,
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

    private const val BACKGROUND_COLOR_RATIO = 0.72f
    private const val SURFACE_LOW_COLOR_RATIO = 0.66f
    private const val SURFACE_COLOR_RATIO = 0.58f
    private const val SURFACE_HIGH_COLOR_RATIO = 0.48f
    private const val OUTLINE_ALPHA = 0.08f
    private const val TEXT_TERTIARY_RATIO = 0.25f
    private const val LIGHT_TEXT_BLEND_RATIO = 0.3f
    private const val ACCENT_LIGHTENING_RATIO = 0.15f
    private const val MAX_ACCENT_LIGHTENING_STEPS = 10
    private const val CONTRASTED_COLOR_THRESHOLD = 4.5f
    private val DYNAMIC_DANGER_COLOR = Color(0xFFFF8A93)
    private val DYNAMIC_WARNING_COLOR = Color(0xFFFFC266)
}
