package com.github.enteraname74.soulsearching.remote.model.music

import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.util.serializer.LocalDateTimeSerializer
import com.github.enteraname74.domain.util.serializer.UUIDSerializer
import com.github.enteraname74.soulsearching.remote.cloud.ServerRoutes
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID

@Serializable
data class RemoteMusic(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val album: String,
    @Serializable(with = UUIDSerializer::class)
    val albumId: UUID,
    val artist: String,
    val path: String,
    val coverPath: String?,
    val duration: Long,
    @Serializable(with = LocalDateTimeSerializer::class)
    val addedDate: LocalDateTime,
    val nbPlayed: Int,
    val isInQuickAccess: Boolean,
) {
    fun toMusic(): Music = Music(
        musicId = id,
        name = name,
        album = album,
        artist = artist,
        cover = Cover.CoverUrl(
            url = coverPath,
        ),
        addedDate = addedDate,
        nbPlayed = nbPlayed,
        isInQuickAccess = isInQuickAccess,
        dataMode = DataMode.Cloud,
        albumId = albumId,
        path = "${ServerRoutes.HOST}/$path",
        folder = "Cloudy",
        duration = duration,
    )
}