package com.github.enteraname74.soulsearching.feature.settings.colortheme.colorseed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.soulsearching.coreui.theme.color.ColorPaletteSeed
import com.github.enteraname74.soulsearching.ext.COLOR_PALETTE_SEED_SETTINGS_ELEMENT
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsColorSeedViewModel(
    private val settings: SoulSearchingSettings,
): ViewModel() {
    val currentSeed: StateFlow<ColorPaletteSeed> =
        settings.getFlowOn(
            settingElement = COLOR_PALETTE_SEED_SETTINGS_ELEMENT,
        ).map { ColorPaletteSeed.fromString(it) ?: ColorPaletteSeed.Default }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = ColorPaletteSeed.Default
            )

    fun setSeed(seed: ColorPaletteSeed) {
        viewModelScope.launch {
            settings.set(
                key = COLOR_PALETTE_SEED_SETTINGS_ELEMENT.key,
                value = seed.toString(),
            )
        }
    }
}