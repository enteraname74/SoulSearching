package com.github.enteraname74.domain.model

import kotlin.uuid.Uuid

/**
 * Used to link a song to its artist.
 */
data class MusicArtist(
    val musicId: Uuid = Uuid.random(),
    val artistId: Uuid = Uuid.random()
) {
    val id: String
        get() = "$musicId$artistId"
}
