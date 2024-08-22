package com.github.enteraname74.soulsearching.coreui.theme.color

import androidx.compose.runtime.Composable
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

@Composable
fun SoulSearchingPalette?.orDefault(): SoulSearchingPalette =
    this ?: SoulSearchingColorTheme.defaultTheme