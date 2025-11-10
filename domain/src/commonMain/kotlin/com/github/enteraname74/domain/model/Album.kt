package com.github.enteraname74.domain.model

import java.time.LocalDateTime
import java.util.UUID

/**
 * Represent an Album and information related to it.
 * It does not possess its musics or its cover directly.
 */
data class Album(
    val albumId: UUID = UUID.randomUUID(),
    val albumName: String,
    val artist: Artist,
    val cover: Cover? = null,
    val addedDate: LocalDateTime = LocalDateTime.now(),
    val nbPlayed: Int = 0,
    val isInQuickAccess: Boolean = false,
) {
    override fun toString(): String =
        "Album(name: $albumName, id: $albumId, artist: $artist)"
}