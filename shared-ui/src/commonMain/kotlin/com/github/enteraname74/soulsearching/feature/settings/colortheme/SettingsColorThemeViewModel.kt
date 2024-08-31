package com.github.enteraname74.soulsearching.feature.settings.colortheme

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class SettingsColorThemeViewModel(
    colorThemeManager: ColorThemeManager,
    private val settings: SoulSearchingSettings,
) : ScreenModel {
    val colorThemeSettingsState: StateFlow<SettingsColorThemeState> = combine(
        colorThemeManager.currentColorThemeSettings,
        settings.getFlowOn(SoulSearchingSettingsKeys.ColorTheme.DYNAMIC_PLAYER_THEME),
        settings.getFlowOn(SoulSearchingSettingsKeys.ColorTheme.DYNAMIC_PLAYLIST_THEME),
        settings.getFlowOn(SoulSearchingSettingsKeys.ColorTheme.DYNAMIC_OTHER_VIEWS_THEME),
    ) { colorThemeSettings, dynamicPlayer, dynamicPlaylists, dynamicOthers ->
        SettingsColorThemeState(
            colorThemeSettings = colorThemeSettings,
            hasPersonalizedDynamicPlayerTheme = dynamicPlayer,
            hasPersonalizedDynamicPlaylistTheme = dynamicPlaylists,
            hasPersonalizedDynamicOtherTheme = dynamicOthers,
        )
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SettingsColorThemeState(),
    )

    /**
     * Update the type of color theme used in the application.
     */
    fun updateColorTheme(newTheme: Int) {
        settings.set(
            key = SoulSearchingSettingsKeys.ColorTheme.COLOR_THEME_KEY.key,
            value = newTheme
        )
    }

    fun togglePersonalizedDynamicPlayerTheme() {
        val previousValue: Boolean = settings.get(SoulSearchingSettingsKeys.ColorTheme.DYNAMIC_PLAYER_THEME)
        settings.set(
            key = SoulSearchingSettingsKeys.ColorTheme.DYNAMIC_PLAYER_THEME.key,
            value = !previousValue
        )
    }

    fun togglePersonalizedDynamicPlaylistTheme() {
        val previousValue: Boolean = settings.get(SoulSearchingSettingsKeys.ColorTheme.DYNAMIC_PLAYLIST_THEME)
        settings.set(
            key = SoulSearchingSettingsKeys.ColorTheme.DYNAMIC_PLAYLIST_THEME.key,
            value = !previousValue
        )
    }

    fun togglePersonalizedDynamicOtherViewsTheme() {
        val previousValue: Boolean = settings.get(SoulSearchingSettingsKeys.ColorTheme.DYNAMIC_OTHER_VIEWS_THEME)
        settings.set(
            key = SoulSearchingSettingsKeys.ColorTheme.DYNAMIC_OTHER_VIEWS_THEME.key,
            value = !previousValue
        )
    }
}
