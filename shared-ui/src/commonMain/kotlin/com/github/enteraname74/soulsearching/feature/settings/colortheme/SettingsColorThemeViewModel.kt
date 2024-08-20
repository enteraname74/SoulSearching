package com.github.enteraname74.soulsearching.feature.settings.colortheme

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import com.github.enteraname74.soulsearching.theme.ColorThemeManager.Companion.PERSONALIZED_DYNAMIC_OTHER_VIEWS_DEFAULT
import com.github.enteraname74.soulsearching.theme.ColorThemeManager.Companion.PERSONALIZED_DYNAMIC_PLAYER_DEFAULT
import com.github.enteraname74.soulsearching.theme.ColorThemeManager.Companion.PERSONALIZED_DYNAMIC_PLAYLISTS_DEFAULT
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
        settings.getFlowOn(
            key = SoulSearchingSettings.DYNAMIC_PLAYER_THEME,
            defaultValue = PERSONALIZED_DYNAMIC_PLAYER_DEFAULT
        ),
        settings.getFlowOn(
            key = SoulSearchingSettings.DYNAMIC_PLAYLIST_THEME,
            defaultValue = PERSONALIZED_DYNAMIC_PLAYLISTS_DEFAULT
        ),
        settings.getFlowOn(
            key = SoulSearchingSettings.DYNAMIC_OTHER_VIEWS_THEME,
            defaultValue = PERSONALIZED_DYNAMIC_OTHER_VIEWS_DEFAULT
        ),
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
        settings.setInt(
            key = SoulSearchingSettings.COLOR_THEME_KEY,
            value = newTheme
        )
    }

    fun togglePersonalizedDynamicPlayerTheme() {
        val previousValue: Boolean = settings.getBoolean(
            key = SoulSearchingSettings.DYNAMIC_PLAYER_THEME,
            defaultValue = PERSONALIZED_DYNAMIC_PLAYER_DEFAULT,
        )
        settings.setBoolean(
            key = SoulSearchingSettings.DYNAMIC_PLAYER_THEME,
            value = !previousValue
        )
    }

    fun togglePersonalizedDynamicPlaylistTheme() {
        val previousValue: Boolean = settings.getBoolean(
            key = SoulSearchingSettings.DYNAMIC_PLAYLIST_THEME,
            defaultValue = PERSONALIZED_DYNAMIC_PLAYLISTS_DEFAULT,
        )
        settings.setBoolean(
            key = SoulSearchingSettings.DYNAMIC_PLAYLIST_THEME,
            value = !previousValue
        )
    }

    fun togglePersonalizedDynamicOtherViewsTheme() {
        val previousValue: Boolean = settings.getBoolean(
            key = SoulSearchingSettings.DYNAMIC_OTHER_VIEWS_THEME,
            defaultValue = PERSONALIZED_DYNAMIC_OTHER_VIEWS_DEFAULT,
        )
        settings.setBoolean(
            key = SoulSearchingSettings.DYNAMIC_OTHER_VIEWS_THEME,
            value = !previousValue
        )
    }
}
