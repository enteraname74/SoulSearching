package com.github.enteraname74.soulsearching.feature.settings.colortheme.themeselection.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.soulsearching.domain.repository.SettingsRepository
import com.github.enteraname74.soulsearching.domain.model.settings.SoulSearchingTheme
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import com.github.enteraname74.soulsearching.theme.DefaultThemeSettings
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsThemeSelectionViewModel(
    private val settingsRepository: SettingsRepository,
    colorThemeManager: ColorThemeManager,
) : ViewModel() {

    val state: StateFlow<DefaultThemeSettings> = colorThemeManager.currentDefaultThemeSettings

    fun selectTheme(themeId: SoulSearchingTheme) {
        viewModelScope.launch { settingsRepository.setUsedTheme(themeId) }
    }

    fun forceDarkTheme(isForcing: Boolean) {
        if (isForcing) {
            viewModelScope.launch { settingsRepository.setForceLightTheme(false) }
        }
        viewModelScope.launch { settingsRepository.setForceDarkTheme(isForcing) }
    }

    fun forceLightTheme(isForcing: Boolean) {
        if (isForcing) {
            viewModelScope.launch { settingsRepository.setForceDarkTheme(false) }
        }
        viewModelScope.launch { settingsRepository.setForceLightTheme(isForcing) }
    }
}
