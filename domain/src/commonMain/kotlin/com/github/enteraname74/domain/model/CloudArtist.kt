package com.github.enteraname74.domain.model

import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID
import kotlin.uuid.Uuid

@Serializable
data class CloudArtist(
    val id: Uuid,
    val name: String,
    val coverPath: String?,
    val nbPlayed: Int = 0,
    val isInQuickAccess: Boolean,
    val lastUpdateAtMillis: Long,
) {
    fun toNewArtist(): Artist =
        Artist(
            artistId = UUID.randomUUID(),
            artistName = name,
            remoteId = id,
            cover = coverPath?.let { Cover.Url(it) },
            addedDate = LocalDateTime.now(),
            nbPlayed = nbPlayed,
            isInQuickAccess = isInQuickAccess,
            lastUpdatedMillis = lastUpdateAtMillis,
        )
}