package com.github.enteraname74.soulsearching.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.enteraname74.soulsearching.coreui.theme.color.*
import com.github.enteraname74.soulsearching.di.injectElement

@Composable
fun SoulSearchingPalette?.orDefault(): SoulSearchingPalette =
    this ?: defaultTheme()

@Composable
private fun defaultTheme(
    colorThemeManager: ColorThemeManager = injectElement(),
): SoulSearchingPalette {
    val settings by colorThemeManager.currentDefaultThemeSettings.collectAsState()
    val theme: SoulSearchingDarkLightTheme = SoulSearchingDarkLightThemes.fromId(
        id = settings.themeId,
    )

    return theme.palette(isInDarkTheme = isInDarkTheme())
}
