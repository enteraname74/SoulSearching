package com.github.enteraname74.domain.model

import com.github.enteraname74.domain.util.DateUtils
import java.time.LocalDateTime
import java.util.UUID
import kotlin.math.max
import kotlin.uuid.Uuid

/**
 * Represent an Album and information related to it.
 * It does not possess its musics or its cover directly.
 */
data class Album(
    val albumId: UUID = UUID.randomUUID(),
    val remoteId: Uuid? = null,
    val albumName: String,
    val artist: Artist,
    val cover: Cover? = null,
    val addedDate: LocalDateTime = LocalDateTime.now(),
    val nbPlayed: Int = 0,
    val isInQuickAccess: Boolean = false,
    val lastUpdateMillis: Long? = DateUtils.now(),
    val scope: Scope = Scope.User,
) {
    override fun toString(): String =
        "Album(name: $albumName, id: $albumId, artist: $artist)"

    fun merge(
        cloudAlbum: CloudAlbum,
        artist: Artist,
        mergeMode: MergeMode,
        scope: Scope,
    ): Album =
        when (mergeMode) {
            MergeMode.LocalFirst ->
                copy(
                    remoteId = cloudAlbum.id,
                )
            MergeMode.RemoteFirst -> copy(
                remoteId = cloudAlbum.id,
                albumName = cloudAlbum.name,
                artist = artist,
                // Prioritize local cover if possible.
                cover = cover?.takeIf { !it.isEmpty() } ?: cloudAlbum.coverPath?.let { Cover.Url(it) },
                nbPlayed = max(nbPlayed, cloudAlbum.nbPlayed),
                isInQuickAccess = cloudAlbum.isInQuickAccess,
                lastUpdateMillis = cloudAlbum.lastUpdateAtMillis,
                scope = scope,
            )
        }
}