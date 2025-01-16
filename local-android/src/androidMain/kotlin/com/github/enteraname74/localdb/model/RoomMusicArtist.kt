package com.github.enteraname74.localdb.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.github.enteraname74.domain.model.MusicArtist
import java.util.*

/**
 * Room representation of a MusicArtist.
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
            entity = RoomArtist::class,
            parentColumns = ["artistId"],
            childColumns = ["artistId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
internal data class RoomMusicArtist(
    @PrimaryKey val id: String,
    @ColumnInfo(index = true) val musicId: UUID = UUID.randomUUID(),
    @ColumnInfo(index = true) val artistId: UUID = UUID.randomUUID()
)

/**
 * Converts a RoomMusicArtist to a MusicArtist.
 */
internal fun RoomMusicArtist.toMusicArtist(): MusicArtist = MusicArtist(
    musicId = musicId,
    artistId = artistId
)

/**
 * Converts a MusicArtist to a RoomMusicArtist.
 */
internal fun MusicArtist.toRoomMusicArtist(): RoomMusicArtist = RoomMusicArtist(
    id = id,
    musicId = musicId,
    artistId = artistId
)