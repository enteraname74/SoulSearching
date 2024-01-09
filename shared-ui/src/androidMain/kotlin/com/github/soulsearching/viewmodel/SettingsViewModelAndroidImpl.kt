package com.github.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import com.github.soulsearching.model.settings.SoulSearchingSettings
import com.github.soulsearching.viewmodel.handler.SettingsViewModelHandler

/**
 * View model for the settings.
 */
class SettingsViewModelAndroidImpl(
    settings: SoulSearchingSettings
): SettingsViewModel {
    override val handler: SettingsViewModelHandler = SettingsViewModelHandler(settings)
}