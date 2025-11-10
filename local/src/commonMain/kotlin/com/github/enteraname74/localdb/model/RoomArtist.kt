package com.github.enteraname74.localdb.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Cover.CoverFile.DevicePathSpec
import java.time.LocalDateTime
import java.util.*

/**
 * Room representation of an Artist.
 */
@Entity
data class RoomArtist(
    @PrimaryKey
    val artistId: UUID = UUID.randomUUID(),
    val artistName: String,
    val coverId: UUID? = null,
    val coverFolderKey: String? = null,
    val addedDate: LocalDateTime = LocalDateTime.now(),
    val nbPlayed: Int = 0,
    val isInQuickAccess: Boolean = false,
)

/**
 * Converts a RoomArtist to an Artist.
 */
internal fun RoomArtist.toArtist(): Artist = Artist(
    artistId = artistId,
    artistName = artistName,
    cover = Cover.CoverFile(
        fileCoverId = coverId,
        devicePathSpec = coverFolderKey?.let { key ->
            DevicePathSpec(
                settingsKey = key,
                dynamicElementName = artistName,
                fallback = Cover.CoverFile(fileCoverId = coverId),
            )
        },
    ),
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
    coverId = (cover as? Cover.CoverFile)?.fileCoverId,
    addedDate = addedDate,
    nbPlayed = nbPlayed,
    isInQuickAccess = isInQuickAccess,
    coverFolderKey = (cover as? Cover.CoverFile)?.devicePathSpec?.settingsKey,
)