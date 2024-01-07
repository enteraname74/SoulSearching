package com.github.enteraname74.domain.model

import java.util.UUID

/**
 * Used to link an album to its corresponding artist.
 */
data class AlbumArtist(
    val id: Long = 0,
    val albumId: UUID = UUID.randomUUID(),
    val artistId: UUID = UUID.randomUUID()
)