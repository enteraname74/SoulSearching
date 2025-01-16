package com.github.enteraname74.localdb.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.github.enteraname74.domain.model.MusicAlbum
import java.util.*

/**
 * Room representation of a MusicAlbum.
 */
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = RoomMusic::class,
            parentColumns = ["musicId"],
            childColumns = ["musicId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RoomAlbum::class,
            parentColumns = ["albumId"],
            childColumns = ["albumId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
internal data class RoomMusicAlbum(
    @PrimaryKey val id: String,
    @ColumnInfo(index = true) val musicId: UUID = UUID.randomUUID(),
    @ColumnInfo(index = true) val albumId: UUID = UUID.randomUUID()
)

/**
 * Converts a RoomMusicAlbum to a MusicAlbum.
 */
internal fun RoomMusicAlbum.toMusicAlbum(): MusicAlbum = MusicAlbum(
    musicId = musicId,
    albumId = albumId
)

/**
 * Converts a MusicAlbum to a RoomMusicAlbum.
 */
internal fun MusicAlbum.toRoomMusicAlbum(): RoomMusicAlbum = RoomMusicAlbum(
    id = id,
    musicId = musicId,
    albumId = albumId
)