package com.github.enteraname74.domain.model

import java.time.LocalDateTime
import java.util.UUID

/**
 * Represent an artist with information related to it.
 * It does not possess its musics or its cover directly.
 */
data class Artist(
    val artistId: UUID = UUID.randomUUID(),
    var artistName: String = "",
    var cover: Cover? = null,
    var addedDate: LocalDateTime = LocalDateTime.now(),
    var nbPlayed: Int = 0,
    var isInQuickAccess: Boolean = false
) {
    fun isComposedOfMultipleArtists(): Boolean =
        artistName.split(",").size > 1

    fun getMultipleArtists(): List<String> =
        artistName.split(",").map { it.trim() }
}