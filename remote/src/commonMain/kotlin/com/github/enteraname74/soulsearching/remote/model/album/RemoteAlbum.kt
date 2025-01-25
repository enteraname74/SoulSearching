package com.github.enteraname74.soulsearching.remote.model.album

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.util.serializer.LocalDateTimeSerializer
import com.github.enteraname74.domain.util.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.*

@Serializable
data class RemoteAlbum(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val coverPath: String?,
    @Serializable(with = LocalDateTimeSerializer::class)
    val addedDate: LocalDateTime,
    val nbPlayed: Int,
    val isInQuickAccess: Boolean,
    @Serializable(with = UUIDSerializer::class)
    val artistId: UUID,
) {
    fun toAlbum(): Album = Album(
        albumId = id,
        albumName = name,
        cover = Cover.CoverUrl(
            url = coverPath,
        ),
        addedDate = addedDate,
        nbPlayed = nbPlayed,
        isInQuickAccess = isInQuickAccess,
        artistId = artistId,
        dataMode = DataMode.Cloud,
    )
}
