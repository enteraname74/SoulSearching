package com.github.enteraname74.domain.model

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.uuid.Uuid

@Serializable
data class CloudAlbum(
    val id: Uuid,
    val name: String,
    val coverPath: String?,
    val artist: CloudArtist,
    val nbPlayed: Int,
    val isInQuickAccess: Boolean,
    val lastUpdateAtMillis: Long,
) {
    fun toNewAlbum(
        artist: Artist,
        scope: Scope,
    ): Album =
        Album(
            albumId = Uuid.random(),
            remoteId = id,
            albumName = name,
            artist = artist,
            cover = coverPath?.let { Cover.Url(it) },
            addedDate = Clock.System.now(),
            nbPlayed = nbPlayed,
            isInQuickAccess = isInQuickAccess,
            lastUpdateMillis = lastUpdateAtMillis,
            scope = scope,
        )
}
