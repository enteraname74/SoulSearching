package com.github.enteraname74.soulsearching.feature.settings.colortheme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.soulsearching.domain.model.settings.ColorThemeType
import com.github.enteraname74.soulsearching.domain.repository.SettingsRepository
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsColorThemeViewModel(
    colorThemeManager: ColorThemeManager,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    val colorThemeSettingsState: StateFlow<SettingsColorThemeState> = combine(
        colorThemeManager.currentColorThemeSettings,
        settingsRepository.observeColorTheme(),
    ) { colorThemeSettings, settings ->
        SettingsColorThemeState(
            colorThemeSettings = colorThemeSettings,
            hasPersonalizedDynamicPlayerTheme = settings.dynamicPlayerTheme,
            hasPersonalizedDynamicPlaylistTheme = settings.dynamicPlaylistTheme,
            hasPersonalizedDynamicOtherTheme = settings.dynamicOtherViewsTheme,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SettingsColorThemeState(),
    )

    /**
     * Update the type of color theme used in the application.
     */
    fun updateColorTheme(newTheme: ColorThemeType) {
        viewModelScope.launch { settingsRepository.setColorThemeType(newTheme) }
    }

    fun togglePersonalizedDynamicPlayerTheme() {
        viewModelScope.launch {
            val previousValue = settingsRepository.getSettings().colorTheme.dynamicPlayerTheme
            settingsRepository.setDynamicPlayerTheme(!previousValue)
        }
    }

    fun togglePersonalizedDynamicPlaylistTheme() {
        viewModelScope.launch {
            val previousValue = settingsRepository.getSettings().colorTheme.dynamicPlaylistTheme
            settingsRepository.setDynamicPlaylistTheme(!previousValue)
        }
    }

    fun togglePersonalizedDynamicOtherViewsTheme() {
        viewModelScope.launch {
            val previousValue = settingsRepository.getSettings().colorTheme.dynamicOtherViewsTheme
            settingsRepository.setDynamicOtherViewsTheme(!previousValue)
        }
    }
}
