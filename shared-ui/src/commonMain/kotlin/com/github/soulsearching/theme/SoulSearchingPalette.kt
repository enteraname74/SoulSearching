package com.github.soulsearching.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Color theme for SoulSearching.
 */
data class SoulSearchingPalette(
    val primary: Color = primaryColorDark,
    val secondary: Color = secondaryColorDark,
    val onPrimary: Color = textColorDark,
    val onSecondary: Color = textColorDark,
    val subText: Color = subTextColorDark
) {
    /**
     * Build a SoulSearchingTheme from a given color scheme.
     */
    constructor(colorScheme: ColorScheme) : this(
        primary = colorScheme.primary,
        secondary = colorScheme.secondary,
        onPrimary = colorScheme.onPrimary,
        onSecondary = colorScheme.onSecondary,
        subText = colorScheme.outline
    )
}
