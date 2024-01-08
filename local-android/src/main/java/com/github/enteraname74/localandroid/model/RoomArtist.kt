package com.github.enteraname74.localandroid.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.enteraname74.domain.model.Artist
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
    var addedDate: LocalDateTime = LocalDateTime.now(),
    var nbPlayed: Int = 0,
    var isInQuickAccess: Boolean = false
)

/**
 * Converts a RoomArtist to an Artist.
 */
internal fun RoomArtist.toArtist(): Artist = Artist(
    artistId = artistId,
    artistName = artistName,
    coverId = coverId,
    addedDate = addedDate,
    nbPlayed = nbPlayed,
    isInQuickAccess = isInQuickAccess
)

/**
 * Converts an Artist to a RoomArtist.
 */
internal fun Artist.toRoomArtist(): RoomArtist = RoomArtist(
    artistId = artistId,
    artistName = artistName,
    coverId = coverId,
    addedDate = addedDate,
    nbPlayed = nbPlayed,
    isInQuickAccess = isInQuickAccess
)