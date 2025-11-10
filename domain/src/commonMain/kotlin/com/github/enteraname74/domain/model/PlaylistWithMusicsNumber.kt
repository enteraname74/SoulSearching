package com.github.enteraname74.domain.model

/**
 * Represent a playlist with the total of songs it possess.
 */
data class PlaylistWithMusicsNumber(
    val playlist: Playlist,
    val musicsNumber : Int,
    val cover: Cover?,
    override val isInQuickAccess: Boolean = playlist.isInQuickAccess,
): QuickAccessible
