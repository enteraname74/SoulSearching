package com.github.enteraname74.soulsearching.feature.settings.colortheme

import com.github.enteraname74.soulsearching.theme.ColorThemeSettings

data class SettingsColorThemeState(
    val colorThemeSettings: ColorThemeSettings = ColorThemeSettings.DynamicTheme,
    val hasPersonalizedDynamicPlayerTheme: Boolean = false,
    val hasPersonalizedDynamicPlaylistTheme: Boolean = false,
    val hasPersonalizedDynamicOtherTheme: Boolean = false,
)
