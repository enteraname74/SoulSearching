package com.github.enteraname74.soulsearching

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.github.enteraname74.soulsearching.coreui.SoulSearchingContext
import com.github.enteraname74.soulsearching.coreui.ext.isDark
import com.github.enteraname74.soulsearching.coreui.theme.color.AnimatedColorPaletteBuilder
import com.github.enteraname74.soulsearching.coreui.theme.color.LocalColors
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import com.github.enteraname74.soulsearching.theme.isInDarkTheme
import com.github.enteraname74.soulsearching.theme.orDefault

@Composable
fun SoulSearchingAppTheme(
    colorThemeManager: ColorThemeManager = injectElement(),
    content: @Composable () -> Unit
) {
    colorThemeManager.isInDarkMode = isInDarkTheme()

    val appColorTheme by colorThemeManager.appColorPalette.collectAsState()

    CompositionLocalProvider(
        LocalColors provides AnimatedColorPaletteBuilder.animate(appColorTheme.orDefault()),
    ) {
        content()
    }
}