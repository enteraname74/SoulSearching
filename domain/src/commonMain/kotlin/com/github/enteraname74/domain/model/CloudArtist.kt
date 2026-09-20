package com.github.enteraname74.domain.model

import kotlinx.serialization.Serializable
import kotlin.time.Clock
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
    fun toNewArtist(
        scope: Scope,
    ): Artist =
        Artist(
            artistId = Uuid.random(),
            artistName = name,
            remoteId = id,
            cover = coverPath?.let { Cover.Url(it, null) },
            addedDate = Clock.System.now(),
            nbPlayed = nbPlayed,
            isInQuickAccess = isInQuickAccess,
            lastUpdatedMillis = lastUpdateAtMillis,
            scope = scope,
        )
}
