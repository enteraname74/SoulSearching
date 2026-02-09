package com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain.state

import com.github.enteraname74.domain.model.Artist

sealed interface SettingsAddMusicsNavigationState {
    data object Idle: SettingsAddMusicsNavigationState
    data class ToMultipleArtists(
        val multipleArtists: List<Artist>,
    ): SettingsAddMusicsNavigationState
    data object NavigateBack: SettingsAddMusicsNavigationState
}