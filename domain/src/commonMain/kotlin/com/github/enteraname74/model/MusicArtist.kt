package com.github.enteraname74.model

import java.util.UUID

/**
 * Used to link a song to its artist.
 */
data class MusicArtist(
    val id: Long = 0,
    val musicId: UUID = UUID.randomUUID(),
    val artistId: UUID = UUID.randomUUID()
)