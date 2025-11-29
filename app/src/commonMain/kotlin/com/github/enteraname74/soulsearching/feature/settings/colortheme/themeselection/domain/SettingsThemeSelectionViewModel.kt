package com.github.enteraname74.soulsearching.feature.settings.colortheme.themeselection.domain

import androidx.lifecycle.ViewModel
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingTheme
import com.github.enteraname74.soulsearching.coreui.theme.color.USED_COLOR_THEME_ID_KEY
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import com.github.enteraname74.soulsearching.theme.DefaultThemeSettings
import kotlinx.coroutines.flow.StateFlow

class SettingsThemeSelectionViewModel(
    private val settings: SoulSearchingSettings,
    colorThemeManager: ColorThemeManager,
) : ViewModel() {

    val state: StateFlow<DefaultThemeSettings> = colorThemeManager.currentDefaultThemeSettings

    fun selectTheme(themeId: SoulSearchingTheme) {
        settings.set(
            key = SoulSearchingSettingsKeys.ColorTheme.USED_COLOR_THEME_ID_KEY.key,
            value = themeId.value,
        )
    }

    fun forceDarkTheme(isForcing: Boolean) {
        if (isForcing) {
            settings.set(
                key = SoulSearchingSettingsKeys.ColorTheme.FORCE_LIGHT_THEME_KEY.key,
                value = false,
            )
        }
        settings.set(
            key = SoulSearchingSettingsKeys.ColorTheme.FORCE_DARK_THEME_KEY.key,
            value = isForcing,
        )
    }

    fun forceLightTheme(isForcing: Boolean) {
        if (isForcing) {
            settings.set(
                key = SoulSearchingSettingsKeys.ColorTheme.FORCE_DARK_THEME_KEY.key,
                value = false,
            )
        }
        settings.set(
            key = SoulSearchingSettingsKeys.ColorTheme.FORCE_LIGHT_THEME_KEY.key,
            value = isForcing,
        )
    }
}