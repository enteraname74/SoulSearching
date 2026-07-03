package com.github.enteraname74.domain.model

import com.github.enteraname74.domain.util.DateUtils
import com.github.enteraname74.domain.util.serializer.InstantSerializer
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid
import kotlin.math.max

/**
 * Represent an artist with information related to it.
 * It does not possess its musics or its cover directly.
 */
@Serializable
data class Artist(
    val artistId: Uuid = Uuid.random(),
    val remoteId: Uuid? = null,
    val artistName: String,
    val cover: Cover? = null,
    @Serializable(with = InstantSerializer::class)
    val addedDate: Instant = Clock.System.now(),
    val nbPlayed: Int = 0,
    val isInQuickAccess: Boolean = false,
    val lastUpdatedMillis: Long? = DateUtils.now(),
    val scope: Scope = Scope.User,
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
        scope: Scope,
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
                scope = scope,
            )
        }
}
