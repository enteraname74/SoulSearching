package com.github.enteraname74.localdb.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import java.time.LocalDateTime
import java.util.UUID

/**
 * Room representation of a song.
 */
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = RoomAlbum::class,
            parentColumns = ["albumId"],
            childColumns = ["albumId"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
internal data class RoomMusic(
    @PrimaryKey
    val musicId: UUID = UUID.randomUUID(),
    var name: String = "",
    val album: String = "",
    val albumArtist: String? = null,
    val artist: String = "",
    var coverId: UUID? = null,
    var duration: Long = 0L,
    var path: String = "",
    var folder: String = "",
    var addedDate: LocalDateTime = LocalDateTime.now(),
    var nbPlayed: Int = 0,
    var isInQuickAccess: Boolean = false,
    var isHidden: Boolean = false,
    var albumPosition: Int?,
    @ColumnInfo(index = true)
    val albumId: UUID
)

/**
 * Converts a RoomMusic to a Music.
 */
internal fun RoomMusic.toMusic(): Music {
    return Music(
        musicId = musicId,
        name = name,
        album = album,
        albumArtist = albumArtist,
        artist = artist,
        cover = Cover.CoverFile(
            initialCoverPath = path,
            fileCoverId = coverId,
        ),
        duration = duration,
        path = path,
        folder = folder,
        addedDate = addedDate,
        nbPlayed = nbPlayed,
        isInQuickAccess = isInQuickAccess,
        isHidden = isHidden,
        albumPosition = albumPosition,
        albumId = albumId,
    )
}

/**
 * Converts a Music to a RoomMusic.
 */
internal fun Music.toRoomMusic(): RoomMusic = RoomMusic(
    musicId = musicId,
    name = name,
    album = album,
    albumArtist = albumArtist,
    artist = artist,
    coverId = (cover as? Cover.CoverFile)?.fileCoverId,
    duration = duration,
    path = path,
    folder = folder,
    addedDate = addedDate,
    nbPlayed = nbPlayed,
    isInQuickAccess = isInQuickAccess,
    isHidden = isHidden,
    albumPosition = albumPosition,
    albumId = albumId,
)