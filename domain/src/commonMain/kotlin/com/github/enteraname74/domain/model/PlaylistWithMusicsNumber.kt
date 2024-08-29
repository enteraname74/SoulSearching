package com.github.enteraname74.domain.model

/**
 * Represent a playlist with the total of songs it possess.
 */
data class PlaylistWithMusicsNumber(
    val playlist: Playlist = Playlist(),
    val musicsNumber : Int = 0,
    override var isInQuickAccess: Boolean = playlist.isInQuickAccess,
): QuickAccessible
