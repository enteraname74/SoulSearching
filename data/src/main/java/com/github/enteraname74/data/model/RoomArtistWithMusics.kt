package com.github.enteraname74.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

internal data class RoomArtistWithMusics(
    @Embedded val roomArtist: RoomArtist = RoomArtist(),
    @Relation(
        parentColumn = "artistId",
        entityColumn = "musicId",
        associateBy = Junction(RoomMusicArtist::class)
    )
    val roomMusics : List<RoomMusic> = emptyList()
)