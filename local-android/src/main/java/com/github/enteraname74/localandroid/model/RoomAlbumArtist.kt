package com.github.enteraname74.localandroid.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.github.enteraname74.domain.model.AlbumArtist
import java.util.*

/**
 * Room representation of an AlbumArtist.
 */
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = RoomAlbum::class,
            parentColumns = ["albumId"],
            childColumns = ["albumId"],
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
internal data class RoomAlbumArtist(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val albumId: UUID = UUID.randomUUID(),
    @ColumnInfo(index = true) val artistId: UUID = UUID.randomUUID()
)

/**
 * Converts a RoomAlbumArtist to an AlbumArtist.
 */
internal fun RoomAlbumArtist.toAlbumArtist(): AlbumArtist = AlbumArtist(
    id = id,
    albumId = albumId,
    artistId = artistId
)

/**
 * Converts an AlbumArtist to a RoomAlbumArtist.
 */
internal fun AlbumArtist.toRoomAlbumArtist(): RoomAlbumArtist = RoomAlbumArtist(
    id = id,
    albumId = albumId,
    artistId = artistId
)