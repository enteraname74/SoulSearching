package com.github.enteraname74.soulsearching.coreui.theme.color

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings

/**
 * Dynamic colors used in the application.
 */
object SoulSearchingColorTheme {
    val colorScheme: SoulSearchingPalette
        @Composable
        get() = LocalColors.current
}

val LocalColors = compositionLocalOf { SoulSearchingPalettes.lightTheme }