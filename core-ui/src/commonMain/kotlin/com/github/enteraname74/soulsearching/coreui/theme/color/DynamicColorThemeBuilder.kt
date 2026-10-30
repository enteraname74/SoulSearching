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

        val text = getTextColor(
            backgroundColor = background,
            lightBlendRatio = TEXT_LIGHT_BLEND_RATIO,
        )
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
            // A deep shade of the cover's hue, like the fixed themes, instead of a muddy blend.
            onAccent = background,
            danger = DYNAMIC_DANGER_COLOR,
            onDanger = background,
            warning = DYNAMIC_WARNING_COLOR,
            onWarning = background,
        )
    }

    /**
     * Brightens the given color until it stands out on the background.
     *
     * The lightness is raised in HSL rather than by blending with white, so the color keeps its saturation
     * and stays vivid instead of turning pastel.
     */
    private fun getAccentColor(
        color: Color,
        backgroundColor: Color,
    ): Color {
        val (hue, initialSaturation, initialLightness) = color.toHsl()
        // Greys stay greys: only colors with an actual hue get their saturation boosted.
        val saturation = if (initialSaturation < GREY_SATURATION_THRESHOLD) {
            initialSaturation
        } else {
            max(initialSaturation, ACCENT_MIN_SATURATION)
        }
        var lightness = max(initialLightness, ACCENT_MIN_LIGHTNESS)
        var accent = Color.hsl(hue = hue, saturation = saturation, lightness = lightness)

        while (
            getContrast(foregroundColor = accent, backgroundColor = backgroundColor) < ACCENT_CONTRAST_THRESHOLD &&
            lightness < ACCENT_MAX_LIGHTNESS
        ) {
            lightness = min(lightness + ACCENT_LIGHTNESS_STEP, ACCENT_MAX_LIGHTNESS)
            accent = Color.hsl(hue = hue, saturation = saturation, lightness = lightness)
        }
        return accent
    }

    /**
     * Converts the color to HSL: hue in [0, 360), saturation and lightness in [0, 1].
     */
    private fun Color.toHsl(): Triple<Float, Float, Float> {
        val maxComponent = max(red, max(green, blue))
        val minComponent = min(red, min(green, blue))
        val lightness = (maxComponent + minComponent) / 2f
        val delta = maxComponent - minComponent

        if (delta == 0f) return Triple(0f, 0f, lightness)

        val saturation = if (lightness > 0.5f) {
            delta / (2f - maxComponent - minComponent)
        } else {
            delta / (maxComponent + minComponent)
        }
        val hueSector = when (maxComponent) {
            red -> (green - blue) / delta + (if (green < blue) 6f else 0f)
            green -> (blue - red) / delta + 2f
            else -> (red - green) / delta + 4f
        }
        return Triple((hueSector * 60f) % 360f, saturation, lightness)
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
        lightBlendRatio: Float = LIGHT_TEXT_BLEND_RATIO,
    ): Color {

        val lightColor = baseLight.blend(
            other = backgroundColor,
            ratio = lightBlendRatio,
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
    private const val TEXT_LIGHT_BLEND_RATIO = 0.18f
    private const val GREY_SATURATION_THRESHOLD = 0.1f
    private const val ACCENT_MIN_SATURATION = 0.6f
    private const val ACCENT_MIN_LIGHTNESS = 0.55f
    private const val ACCENT_MAX_LIGHTNESS = 0.85f
    private const val ACCENT_LIGHTNESS_STEP = 0.03f
    private const val ACCENT_CONTRAST_THRESHOLD = 7f
    private const val CONTRASTED_COLOR_THRESHOLD = 4.5f
    private val DYNAMIC_DANGER_COLOR = Color(0xFFFF8A93)
    private val DYNAMIC_WARNING_COLOR = Color(0xFFFFC266)
}
