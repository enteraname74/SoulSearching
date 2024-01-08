package com.github.enteraname74.model

/**
 * Represent an album with its artist.
 */
data class AlbumWithArtist(
    val album: Album = Album(),
    val artist: Artist? = Artist()
)