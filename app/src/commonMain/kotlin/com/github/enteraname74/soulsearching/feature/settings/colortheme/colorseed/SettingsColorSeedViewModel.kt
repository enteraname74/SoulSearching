package com.github.enteraname74.soulsearching.feature.settings.colortheme.colorseed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.soulsearching.domain.repository.SettingsRepository
import com.github.enteraname74.soulsearching.coreui.theme.color.ColorPaletteSeed
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsColorSeedViewModel(
    private val settingsRepository: SettingsRepository,
): ViewModel() {
    val currentSeed: StateFlow<ColorPaletteSeed> =
        settingsRepository.observeColorTheme().map { ColorPaletteSeed.fromString(it.paletteSeed) ?: ColorPaletteSeed.Default }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = ColorPaletteSeed.Default
            )

    fun setSeed(seed: ColorPaletteSeed) {
        viewModelScope.launch {
            settingsRepository.setPaletteSeed(seed.toString())
        }
    }
}
