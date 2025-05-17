package com.github.enteraname74.localdb.model

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Room representation of an AlbumWithArtist.
 */
internal data class RoomAlbumWithArtist(
    @Embedded val roomAlbum: RoomAlbum,
    @Relation(
        parentColumn = "artistId",
        entityColumn = "artistId",
    )
    val roomArtist: RoomArtist?
)