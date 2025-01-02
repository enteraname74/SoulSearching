package com.github.enteraname74.domain.model

import com.github.enteraname74.domain.util.serializer.LocalDateTimeSerializer
import com.github.enteraname74.domain.util.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import java.time.LocalDateTime
import java.util.UUID

/**
 * Represent an artist with information related to it.
 * It does not possess its musics or its cover directly.
 */
@Serializable
data class Artist(
    @Serializable(with = UUIDSerializer::class)
    val artistId: UUID = UUID.randomUUID(),
    var artistName: String = "",
    var cover: Cover? = null,
    @Serializable(with = LocalDateTimeSerializer::class)
    var addedDate: LocalDateTime = LocalDateTime.now(),
    var nbPlayed: Int = 0,
    var isInQuickAccess: Boolean = false
) {
    fun isComposedOfMultipleArtists(): Boolean =
        artistName.split(",").size > 1

    fun getMultipleArtists(): List<String> =
        artistName.split(",").map { it.trim() }
}