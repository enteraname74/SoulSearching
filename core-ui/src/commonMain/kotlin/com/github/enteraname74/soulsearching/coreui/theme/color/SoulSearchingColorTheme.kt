package com.github.enteraname74.soulsearching.coreui.theme.color

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf

/**
 * Dynamic colors used in the application.
 */
object SoulSearchingColorTheme {
    val colorScheme: SoulSearchingPalette
        @Composable
        get() = LocalColors.current
}

val LocalColors = compositionLocalOf { SoulSearchingPalettes.lightTheme }