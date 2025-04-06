package com.github.enteraname74.localdb.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.DataMode
import java.time.LocalDateTime
import java.util.*

/**
 * Room representation of an Artist.
 */
@Entity
internal data class RoomArtist(
    @PrimaryKey
    val artistId: UUID = UUID.randomUUID(),
    var artistName: String = "",
    var coverId: UUID? = null,
    var coverUrl: String? = null,
    var addedDate: LocalDateTime = LocalDateTime.now(),
    var nbPlayed: Int = 0,
    var isInQuickAccess: Boolean = false,
    val dataMode: String = DataMode.Local.value,
) {
    fun buildCover(): Cover? =
        if (coverUrl != null) {
            Cover.CoverUrl(
                url = coverUrl
            )
        } else {
            coverId?.let {
                Cover.CoverFile(
                    fileCoverId = it,
                )
            }
        }
}

/**
 * Converts a RoomArtist to an Artist.
 */
internal fun RoomArtist.toArtist(): Artist = Artist(
    artistId = artistId,
    artistName = artistName,
    cover = buildCover(),
    addedDate = addedDate,
    nbPlayed = nbPlayed,
    isInQuickAccess = isInQuickAccess,
    dataMode = DataMode.fromString(dataMode) ?: DataMode.Local,
)

/**
 * Converts an Artist to a RoomArtist.
 */
internal fun Artist.toRoomArtist(): RoomArtist = RoomArtist(
    artistId = artistId,
    artistName = artistName,
    coverId = (cover as? Cover.CoverFile)?.fileCoverId,
    coverUrl = (cover as? Cover.CoverUrl)?.url,
    addedDate = addedDate,
    nbPlayed = nbPlayed,
    isInQuickAccess = isInQuickAccess,
    dataMode = dataMode.value,
)