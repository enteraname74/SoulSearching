package com.github.enteraname74.soulsearching.remote.model
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.util.serializer.LocalDateTimeSerializer
import com.github.enteraname74.domain.util.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.*

@Serializable
data class RemoteArtist(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val coverPath: String?,
    @Serializable(with = LocalDateTimeSerializer::class)
    var addedDate: LocalDateTime,
    var nbPlayed: Int,
    var isInQuickAccess: Boolean,
) {
    fun toArtist(): Artist = Artist(
        artistId = id,
        artistName = name,
        cover = Cover.CoverUrl(
            url = coverPath,
        ),
        addedDate = addedDate,
        nbPlayed = nbPlayed,
        isInQuickAccess = isInQuickAccess,
        dataMode = DataMode.Cloud,
    )
}
