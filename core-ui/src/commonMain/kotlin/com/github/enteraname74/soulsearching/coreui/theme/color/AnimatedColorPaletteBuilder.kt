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
            subSecondaryText = palette.subSecondaryText.animated(label = SUB_SECONDARY_TEXT_COLOR_LABEL),
            subPrimaryText = palette.subPrimaryText.animated(label = SUB_PRIMARY_TEXT_COLOR_LABEL),
        )

    private const val PRIMARY_COLOR_LABEL = "PRIMARY_COLOR_LABEL"
    private const val ON_PRIMARY_COLOR_LABEL = "ON_PRIMARY_COLOR_LABEL"
    private const val SECONDARY_COLOR_LABEL = "SECONDARY_COLOR_LABEL"
    private const val ON_SECONDARY_COLOR_LABEL = "ON_SECONDARY_COLOR_LABEL"
    private const val SUB_PRIMARY_TEXT_COLOR_LABEL = "SUB_PRIMARY_TEXT_COLOR_LABEL"
    private const val SUB_SECONDARY_TEXT_COLOR_LABEL = "SUB_SECONDARY_TEXT_COLOR_LABEL"
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