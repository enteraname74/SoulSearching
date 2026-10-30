package com.github.enteraname74.soulsearching.coreui.theme.color

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.github.enteraname74.soulsearching.coreui.UiConstants

object AnimatedColorPaletteBuilder {
    @Composable
    fun animate(palette: SoulSearchingPalette): SoulSearchingPalette =
        SoulSearchingPalette(
            background = palette.background.animated(label = BACKGROUND_COLOR_LABEL),
            surfaceLow = palette.surfaceLow.animated(label = SURFACE_LOW_COLOR_LABEL),
            surface = palette.surface.animated(label = SURFACE_COLOR_LABEL),
            surfaceHigh = palette.surfaceHigh.animated(label = SURFACE_HIGH_COLOR_LABEL),
            outline = palette.outline.animated(label = OUTLINE_COLOR_LABEL),
            text = palette.text.animated(label = TEXT_COLOR_LABEL),
            textSecondary = palette.textSecondary.animated(label = TEXT_SECONDARY_COLOR_LABEL),
            textTertiary = palette.textTertiary.animated(label = TEXT_TERTIARY_COLOR_LABEL),
            accent = palette.accent.animated(label = ACCENT_COLOR_LABEL),
            onAccent = palette.onAccent.animated(label = ON_ACCENT_COLOR_LABEL),
            danger = palette.danger.animated(label = DANGER_COLOR_LABEL),
            onDanger = palette.onDanger.animated(label = ON_DANGER_COLOR_LABEL),
            warning = palette.warning.animated(label = WARNING_COLOR_LABEL),
            onWarning = palette.onWarning.animated(label = ON_WARNING_COLOR_LABEL),
        )

    private const val BACKGROUND_COLOR_LABEL = "BACKGROUND_COLOR_LABEL"
    private const val SURFACE_LOW_COLOR_LABEL = "SURFACE_LOW_COLOR_LABEL"
    private const val SURFACE_COLOR_LABEL = "SURFACE_COLOR_LABEL"
    private const val SURFACE_HIGH_COLOR_LABEL = "SURFACE_HIGH_COLOR_LABEL"
    private const val OUTLINE_COLOR_LABEL = "OUTLINE_COLOR_LABEL"
    private const val TEXT_COLOR_LABEL = "TEXT_COLOR_LABEL"
    private const val TEXT_SECONDARY_COLOR_LABEL = "TEXT_SECONDARY_COLOR_LABEL"
    private const val TEXT_TERTIARY_COLOR_LABEL = "TEXT_TERTIARY_COLOR_LABEL"
    private const val ACCENT_COLOR_LABEL = "ACCENT_COLOR_LABEL"
    private const val ON_ACCENT_COLOR_LABEL = "ON_ACCENT_COLOR_LABEL"
    private const val DANGER_COLOR_LABEL = "DANGER_COLOR_LABEL"
    private const val ON_DANGER_COLOR_LABEL = "ON_DANGER_COLOR_LABEL"
    private const val WARNING_COLOR_LABEL = "WARNING_COLOR_LABEL"
    private const val ON_WARNING_COLOR_LABEL = "ON_WARNING_COLOR_LABEL"
}

@Composable
fun Color.animated(
    label: String,
    animationDuration: Int = UiConstants.AnimationDuration.medium,
) = animateColorAsState(
    targetValue = this,
    animationSpec = tween(animationDuration),
    label = label,
).value
