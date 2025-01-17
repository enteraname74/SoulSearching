package com.github.enteraname74.domain.model

import java.util.UUID

/**
 * Used to link an album to its corresponding artist.
 */
data class AlbumArtist(
    val albumId: UUID = UUID.randomUUID(),
    val artistId: UUID = UUID.randomUUID()
) {
    val id: String
        get() = "$albumId$artistId"
}