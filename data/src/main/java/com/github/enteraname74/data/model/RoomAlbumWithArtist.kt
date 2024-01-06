package com.github.enteraname74.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

internal data class RoomAlbumWithArtist(
    @Embedded val roomAlbum: RoomAlbum = RoomAlbum(),
    @Relation(
        parentColumn = "albumId",
        entityColumn = "artistId",
        associateBy = Junction(RoomAlbumArtist::class)
    )
    val roomArtist: RoomArtist? = RoomArtist()
)