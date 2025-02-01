package com.github.enteraname74.soulsearching.remote.model.playlist

import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.util.serializer.LocalDateTimeSerializer
import com.github.enteraname74.domain.util.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID

@Serializable
data class RemotePlaylist(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val coverPath: String?,
    val isFavorite: Boolean,
    @Serializable(with = LocalDateTimeSerializer::class)
    val addedDate: LocalDateTime,
    val nbPlayed: Int,
    val isInQuickAccess: Boolean,
) {
    fun toPlaylist(): Playlist = Playlist(
        playlistId = id,
        name = name,
        cover = Cover.CoverUrl(
            url = coverPath,
        ),
        isFavorite = isFavorite,
        addedDate = addedDate,
        nbPlayed = nbPlayed,
        isInQuickAccess = isInQuickAccess,
        dataMode = DataMode.Cloud,
    )
}
