package com.github.enteraname74.soulsearching.feature.settings.advanced

sealed interface SettingsAdvancedAction {
    data object ToMultipleArtists: SettingsAdvancedAction
    data object ToggleMusicsCover: SettingsAdvancedAction
    data object TogglePlaylistsCovers: SettingsAdvancedAction
    data object ToggleAlbumsCovers: SettingsAdvancedAction
    data object ToggleArtistsCovers: SettingsAdvancedAction
    data object ToggleLyricsPermission: SettingsAdvancedAction
    data object ToggleGithubReleaseFetchPermission: SettingsAdvancedAction
    data object ShowLyricsPermissionDialog: SettingsAdvancedAction
    data object ShowGitHubReleasePermissionDialog: SettingsAdvancedAction
    data object ToggleExpandReloadImage: SettingsAdvancedAction
    data object ReloadImages: SettingsAdvancedAction
}