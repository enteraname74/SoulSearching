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
            primary = palette.primary.animated(label = PRIMARY_COLOR_LABEL),
            secondary = palette.secondary.animated(label = SECONDARY_COLOR_LABEL),
            onPrimary = palette.onPrimary.animated(label = ON_PRIMARY_COLOR_LABEL),
            onSecondary = palette.onSecondary.animated(label = ON_SECONDARY_COLOR_LABEL),
            subText = palette.subText.animated(label = SUB_TEXT_COLOR_LABEL),
        )

    @Composable
    private fun Color.animated(label: String) = animateColorAsState(
        targetValue = this,
        animationSpec = tween(UiConstants.AnimationDuration.short),
        label = label,
    ).value

    private const val PRIMARY_COLOR_LABEL = "PRIMARY_COLOR_LABEL"
    private const val ON_PRIMARY_COLOR_LABEL = "ON_PRIMARY_COLOR_LABEL"
    private const val SECONDARY_COLOR_LABEL = "SECONDARY_COLOR_LABEL"
    private const val ON_SECONDARY_COLOR_LABEL = "ON_SECONDARY_COLOR_LABEL"
    private const val SUB_TEXT_COLOR_LABEL = "SUB_TEXT_COLOR_LABEL"
}