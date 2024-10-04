package com.github.enteraname74.localdb.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.enteraname74.domain.model.Music
import java.time.LocalDateTime
import java.util.UUID

/**
 * Room representation of a song.
 */
@Entity
internal data class RoomMusic(
    @PrimaryKey
    val musicId: UUID = UUID.randomUUID(),
    var name: String = "",
    val album: String = "",
    val artist: String = "",
    val initialCoverPath: String? = null,
    var coverId: UUID? = null,
    var duration: Long = 0L,
    var path: String = "",
    var folder: String = "",
    var addedDate: LocalDateTime = LocalDateTime.now(),
    var nbPlayed: Int = 0,
    var isInQuickAccess: Boolean = false,
    var isHidden: Boolean = false
)

/**
 * Converts a RoomMusic to a Music.
 */
internal fun RoomMusic.toMusic(): Music = Music(
    musicId = musicId,
    name = name,
    album = album,
    artist = artist,
    coverId = coverId,
    duration = duration,
    path = path,
    folder = folder,
    addedDate = addedDate,
    nbPlayed = nbPlayed,
    isInQuickAccess = isInQuickAccess,
    isHidden = isHidden,
    initialCoverPath = initialCoverPath,
)

/**
 * Converts a Music to a RoomMusic.
 */
internal fun Music.toRoomMusic(): RoomMusic = RoomMusic(
    musicId = musicId,
    name = name,
    album = album,
    artist = artist,
    coverId = coverId,
    duration = duration,
    path = path,
    folder = folder,
    addedDate = addedDate,
    nbPlayed = nbPlayed,
    isInQuickAccess = isInQuickAccess,
    isHidden = isHidden,
    initialCoverPath = initialCoverPath,
)