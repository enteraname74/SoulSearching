package com.github.enteraname74.domain.model

import java.util.UUID

/**
 * Used to link a song to its corresponding album.
 */
data class MusicAlbum(
    val id: Long = 0,
    val musicId: UUID = UUID.randomUUID(),
    val albumId: UUID = UUID.randomUUID()
)