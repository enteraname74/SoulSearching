package com.github.soulsearching.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.github.soulsearching.classes.SharedPrefUtils
import com.github.soulsearching.classes.enumsAndTypes.ColorThemeType

class SettingsViewModel: ViewModel() {
    var colorTheme by mutableStateOf(ColorThemeType.SYSTEM)
    var isDynamicPlayerThemeSelected by mutableStateOf(false)

    fun updateColorTheme(newTheme: Int) {
        colorTheme = newTheme
        SharedPrefUtils.updateIntValue(
            keyToUpdate = SharedPrefUtils.COLOR_THEME_KEY,
            newValue = newTheme
        )
    }

    fun isPersonalizedDynamicPlayerThemeOn(): Boolean {
        return (colorTheme == ColorThemeType.PERSONALIZED && isDynamicPlayerThemeSelected) || colorTheme == ColorThemeType.DYNAMIC
    }

    fun toggleDynamicPlayer() {
        isDynamicPlayerThemeSelected = !isDynamicPlayerThemeSelected
        SharedPrefUtils.updateBooleanValue(
            keyToUpdate = SharedPrefUtils.DYNAMIC_PLAYER_THEME,
            newValue = isDynamicPlayerThemeSelected
        )
    }
}