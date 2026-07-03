package com.github.enteraname74.domain.model

import kotlin.uuid.Uuid

/**
 * Used to link a song to its corresponding album.
 */
data class MusicAlbum(
    val id: Long = 0,
    val musicId: Uuid = Uuid.random(),
    val albumId: Uuid = Uuid.random()
)
