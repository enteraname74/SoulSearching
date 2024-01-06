package com.github.enteraname74.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

internal data class RoomAlbumWithMusics(
    @Embedded val roomAlbum: RoomAlbum = RoomAlbum(),
    @Relation(
        parentColumn = "albumId",
        entityColumn = "musicId",
        associateBy = Junction(RoomMusicAlbum::class)
    )
    val roomMusics : List<RoomMusic> = emptyList(),
    @Relation(
        parentColumn = "albumId",
        entityColumn = "artistId",
        associateBy = Junction(RoomAlbumArtist::class)
    )
    val roomArtist: RoomArtist? = RoomArtist()
) {
    fun toAlbumWithArtist() : RoomAlbumWithArtist = RoomAlbumWithArtist(
        roomAlbum = roomAlbum,
        roomArtist = roomArtist
    )
}