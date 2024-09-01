package com.github.enteraname74.domain.model

/**
 * Represent an album with its artist.
 */
data class AlbumWithArtist(
    val album: Album = Album(),
    val artist: Artist? = Artist(),
    override var isInQuickAccess: Boolean = album.isInQuickAccess,
): QuickAccessible