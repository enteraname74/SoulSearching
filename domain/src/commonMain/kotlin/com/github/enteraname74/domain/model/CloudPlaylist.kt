package com.github.enteraname74.domain.model

import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
data class CloudPlaylist(
    val playlist: Info,
    val musicIds: List<String>,
) {
    @Serializable
    data class Info(
        val id: Uuid = Uuid.random(),
        val userId: Uuid,
        val name: String,
        val coverPath: String?,
        val isFavorite: Boolean,
        val addedDateMillis: Long,
        val nbPlayed: Int,
        val isInQuickAccess: Boolean,
        val lastUpdateAtMillis: Long,
    )

    fun toNewPlaylist(): Playlist =
        with(playlist) {
            Playlist(
                playlistId = Uuid.random(),
                remoteId = id,
                name = name,
                cover = coverPath?.let { Cover.Url(it, null) },
                isFavorite = isFavorite,
                addedDate = Instant.fromEpochMilliseconds(addedDateMillis),
                nbPlayed = nbPlayed,
                isInQuickAccess = isInQuickAccess,
                lastUpdatedMillis = lastUpdateAtMillis,
            )
        }
}
