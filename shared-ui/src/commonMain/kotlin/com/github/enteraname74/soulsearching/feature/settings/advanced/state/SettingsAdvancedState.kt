package com.github.enteraname74.soulsearching.feature.settings.advanced.state

data class SettingsAdvancedState(
    val isImageReloadPanelExpanded: Boolean = false,
    val shouldReloadSongsCovers: Boolean = false,
    val shouldDeletePlaylistsCovers: Boolean = false,
    val shouldReloadAlbumsCovers: Boolean = false,
    val shouldReloadArtistsCovers: Boolean = false,
)