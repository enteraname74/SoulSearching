package com.github.enteraname74.soulsearching.feature.settings.statistics

import kotlin.uuid.Uuid

interface SettingsStatisticsNavScope {
    fun toAlbum(albumId: Uuid)
    fun toArtist(artistId: Uuid)
    fun toPlaylist(playlistId: Uuid)
    fun navigateBack()
}