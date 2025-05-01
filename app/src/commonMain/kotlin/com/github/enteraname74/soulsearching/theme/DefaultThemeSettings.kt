package com.github.enteraname74.soulsearching.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingTheme
import com.github.enteraname74.soulsearching.di.injectElement

data class DefaultThemeSettings(
    val themeId: SoulSearchingTheme,
    val forceDarkTheme: Boolean,
    val forceLightTheme: Boolean,
)


@Composable
fun isInDarkTheme(
    colorThemeManager: ColorThemeManager = injectElement()
): Boolean {
    val currentDefaultSettings by colorThemeManager.currentDefaultThemeSettings.collectAsState()

    if (currentDefaultSettings.forceLightTheme) return false
    if (currentDefaultSettings.forceDarkTheme) return true

    return isSystemInDarkTheme()
}