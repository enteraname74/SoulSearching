package com.github.enteraname74.soulsearching

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.enteraname74.soulsearching.coreui.theme.color.AnimatedColorPaletteBuilder
import com.github.enteraname74.soulsearching.coreui.theme.color.LocalColors
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.Typography
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import com.github.enteraname74.soulsearching.theme.isInDarkTheme
import com.github.enteraname74.soulsearching.theme.orDefault

@Composable
internal fun SoulSearchingTheme(
    colorThemeManager: ColorThemeManager = injectElement(),
    content: @Composable () -> Unit
) {
    colorThemeManager.isInDarkMode = isInDarkTheme()

    val appColorTheme by colorThemeManager.appColorPalette.collectAsState()

    CompositionLocalProvider(
        LocalColors provides AnimatedColorPaletteBuilder.animate(appColorTheme.orDefault()),
    ) {
        MaterialTheme(
            typography = Typography,
            content = content
        )
    }
}
