package com.github.enteraname74.domain.model

/**
 * Represent an album with its artist.
 */
data class AlbumWithArtist(
    val album: Album,
    val artist: Artist?,
    val cover: Cover?,
)