package com.github.enteraname74.localandroid.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.github.enteraname74.model.AlbumWithArtist

/**
 * Room representation of an AlbumWithArtist.
 */
internal data class RoomAlbumWithArtist(
    @Embedded val roomAlbum: RoomAlbum = RoomAlbum(),
    @Relation(
        parentColumn = "albumId",
        entityColumn = "artistId",
        associateBy = Junction(RoomAlbumArtist::class)
    )
    val roomArtist: RoomArtist? = RoomArtist()
)

/**
 * Converts a RoomAlbumWithArtist to an AlbumWithArtist.
 */
internal fun RoomAlbumWithArtist.toAlbumWithArtist(): AlbumWithArtist = AlbumWithArtist(
    album = roomAlbum.toAlbum(),
    artist = roomArtist?.toArtist()
)

/**
 * Converts an AlbumWithArtist to a RoomAlbumWithArtist.
 */
internal fun AlbumWithArtist.toRoomAlbumWithArtist(): RoomAlbumWithArtist = RoomAlbumWithArtist(
    roomAlbum = album.toRoomAlbum(),
    roomArtist = artist?.toRoomArtist()
)