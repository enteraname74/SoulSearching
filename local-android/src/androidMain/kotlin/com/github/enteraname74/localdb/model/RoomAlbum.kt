package com.github.enteraname74.localdb.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Cover
import java.time.LocalDateTime
import java.util.UUID

/**
 * Room representation of an Album.
 */
@Entity
internal data class RoomAlbum(
    @PrimaryKey
    val albumId: UUID = UUID.randomUUID(),
    var albumName: String = "",
    var coverId: UUID? = null,
    var addedDate: LocalDateTime = LocalDateTime.now(),
    var nbPlayed: Int = 0,
    var isInQuickAccess: Boolean = false
)

/**
 * Converts a RoomAlbum to an Album.
 */
internal fun RoomAlbum.toAlbum(): Album = Album(
    albumId = albumId,
    albumName = albumName,
    cover = Cover.FileCover(fileCoverId = coverId),
    addedDate = addedDate,
    nbPlayed = nbPlayed,
    isInQuickAccess = isInQuickAccess
)

/**
 * Converts an Album to a RoomAlbum
 */
internal fun Album.toRoomAlbum(): RoomAlbum = RoomAlbum(
    albumId = albumId,
    albumName = albumName,
    coverId = (cover as? Cover.FileCover)?.fileCoverId,
    addedDate = addedDate,
    nbPlayed = nbPlayed,
    isInQuickAccess = isInQuickAccess
)