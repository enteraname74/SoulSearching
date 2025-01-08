package com.github.enteraname74.soulsearching.remote.model

import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.util.serializer.LocalDateTimeSerializer
import com.github.enteraname74.domain.util.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID

@Serializable
data class RemoteMusic(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val album: String,
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
    )
}