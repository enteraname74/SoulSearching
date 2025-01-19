package com.github.enteraname74.domain.model

import java.util.UUID

/**
 * Used to link a song to its artist.
 */
data class MusicArtist(
    val musicId: UUID = UUID.randomUUID(),
    val artistId: UUID = UUID.randomUUID(),
    val dataMode: DataMode,
) {
    val id: String
        get() = "$musicId$artistId"
}