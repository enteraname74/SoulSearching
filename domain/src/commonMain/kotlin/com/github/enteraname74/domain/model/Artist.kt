package com.github.enteraname74.domain.model

import com.github.enteraname74.domain.util.DateUtils
import com.github.enteraname74.domain.util.serializer.LocalDateTimeSerializer
import com.github.enteraname74.domain.util.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.*
import kotlin.math.max
import kotlin.uuid.Uuid

/**
 * Represent an artist with information related to it.
 * It does not possess its musics or its cover directly.
 */
@Serializable
data class Artist(
    @Serializable(with = UUIDSerializer::class)
    val artistId: UUID = UUID.randomUUID(),
    val remoteId: Uuid? = null,
    val artistName: String,
    val cover: Cover? = null,
    @Serializable(with = LocalDateTimeSerializer::class)
    val addedDate: LocalDateTime = LocalDateTime.now(),
    val nbPlayed: Int = 0,
    val isInQuickAccess: Boolean = false,
    val lastUpdatedMillis: Long? = DateUtils.now(),
) {
    fun isComposedOfMultipleArtists(): Boolean =
        artistName.split(",", "&").size > 1

    fun getMultipleArtists(): List<String> =
        artistName.split(",", "&").map { it.trim() }

    override fun toString(): String =
        "Artist(name: $artistName, id: $artistId)"

    fun merge(
        cloudArtist: CloudArtist,
        mergeMode: MergeMode,
    ): Artist =
        when (mergeMode) {
            MergeMode.LocalFirst -> copy(
                remoteId = cloudArtist.id,
            )
            MergeMode.RemoteFirst -> copy(
                remoteId = cloudArtist.id,
                artistName = cloudArtist.name,
                // Prioritize local cover if possible.
                cover = cover?.takeIf { !it.isEmpty() } ?: cloudArtist.coverPath?.let { Cover.Url(it) },
                nbPlayed = max(nbPlayed, cloudArtist.nbPlayed),
                isInQuickAccess = cloudArtist.isInQuickAccess,
                lastUpdatedMillis = cloudArtist.lastUpdateAtMillis,
            )
        }
}