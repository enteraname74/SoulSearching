package com.github.enteraname74.soulsearching

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import com.github.enteraname74.soulsearching.coreui.theme.color.AnimatedColorPaletteBuilder
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import com.github.enteraname74.soulsearching.coreui.theme.color.LocalColors
import com.github.enteraname74.soulsearching.coreui.theme.color.orDefault
import com.github.enteraname74.soulsearching.di.injectElement

@Composable
fun SoulSearchingAppTheme(
    colorThemeManager: ColorThemeManager = injectElement(),
    content: @Composable () -> Unit
) {
    colorThemeManager.isInDarkMode = isSystemInDarkTheme()

    val appColorTheme by colorThemeManager.appColorPalette.collectAsState()

    CompositionLocalProvider(
        LocalColors provides AnimatedColorPaletteBuilder.animate(appColorTheme.orDefault()),
    ) {
        content()
    }
}