package com.github.enteraname74.domain.model

import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID
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
    ): Album =
        Album(
            albumId = UUID.randomUUID(),
            remoteId = id,
            albumName = name,
            artist = artist,
            cover = coverPath?.let { Cover.Url(it) },
            addedDate = LocalDateTime.now(),
            nbPlayed = nbPlayed,
            isInQuickAccess = isInQuickAccess,
            lastUpdateMillis = lastUpdateAtMillis,
        )
}