package com.github.soulsearching.colortheme.domain.model

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
)