package com.github.enteraname74.soulsearching.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.soulsearching.coreui.theme.color.*
import com.github.enteraname74.soulsearching.di.injectElement

@Composable
fun SoulSearchingPalette?.orDefault(): SoulSearchingPalette =
    this ?: defaultTheme()

@Composable
private fun defaultTheme(
    settings: SoulSearchingSettings = injectElement(),
): SoulSearchingPalette {
    val themeId: String = settings.get(SoulSearchingSettingsKeys.ColorTheme.USED_COLOR_THEME_ID_KEY)
    val theme: SoulSearchingDarkLightTheme = SoulSearchingDarkLightThemes.fromId(
        id = SoulSearchingTheme.from(id = themeId)
    )

    return theme.palette(isInDarkTheme = isInDarkTheme())
}