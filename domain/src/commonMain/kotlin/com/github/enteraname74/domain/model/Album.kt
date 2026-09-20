package com.github.enteraname74.domain.model

import com.github.enteraname74.domain.util.DateUtils
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid
import kotlin.math.max

/**
 * Represent an Album and information related to it.
 * It does not possess its musics or its cover directly.
 */
data class Album(
    val albumId: Uuid = Uuid.random(),
    val remoteId: Uuid? = null,
    val albumName: String,
    val artist: Artist,
    val cover: Cover? = null,
    val addedDate: Instant = Clock.System.now(),
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
                    cover = cover?.takeIf { !it.isEmpty() } ?: cloudAlbum.coverPath?.let { Cover.Url(it, null) },
                )
            MergeMode.RemoteFirst -> copy(
                remoteId = cloudAlbum.id,
                albumName = cloudAlbum.name,
                artist = artist,
                // Prioritize local cover if possible.
                cover = cover?.takeIf { !it.isEmpty() } ?: cloudAlbum.coverPath?.let { Cover.Url(it, null) },
                nbPlayed = max(nbPlayed, cloudAlbum.nbPlayed),
                isInQuickAccess = cloudAlbum.isInQuickAccess,
                lastUpdateMillis = cloudAlbum.lastUpdateAtMillis,
                scope = scope,
            )
        }
}
