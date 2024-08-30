package com.github.enteraname74.soulsearching.feature.settings.statistics.domain

data class SettingsStatisticsState(
    val mostListenedMusics: List<ListenedElement> = emptyList(),
    val mostListenedAlbums: List<ListenedElement> = emptyList(),
    val mostListenedArtists: List<ListenedElement> = emptyList(),
    val mostListenedPlaylists: List<ListenedElement> = emptyList(),
)