package com.github.enteraname74.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

internal data class AlbumWithArtist(
    @Embedded val album: Album = Album(),
    @Relation(
        parentColumn = "albumId",
        entityColumn = "artistId",
        associateBy = Junction(AlbumArtist::class)
    )
    val artist: Artist? = Artist()
)