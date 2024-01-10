package com.github.soulsearching.viewmodel

import com.github.soulsearching.model.settings.SoulSearchingSettings
import com.github.soulsearching.theme.ColorThemeManager
import com.github.soulsearching.viewmodel.handler.SettingsViewModelHandler

/**
 * View model for the settings.
 */
class SettingsViewModelAndroidImpl(
    settings: SoulSearchingSettings,
    colorThemeManager: ColorThemeManager
): SettingsViewModel {
    override val handler: SettingsViewModelHandler = SettingsViewModelHandler(
        settings = settings,
        colorThemeManager = colorThemeManager
    )
}