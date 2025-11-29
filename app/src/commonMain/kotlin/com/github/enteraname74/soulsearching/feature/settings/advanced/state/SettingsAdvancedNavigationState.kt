package com.github.enteraname74.soulsearching.feature.settings.advanced.state

sealed interface SettingsAdvancedNavigationState {
    data object Idle: SettingsAdvancedNavigationState
    data object NavigateBack: SettingsAdvancedNavigationState
    data object ToMultipleArtists: SettingsAdvancedNavigationState
    data object ToArtistCoverMethod: SettingsAdvancedNavigationState
}