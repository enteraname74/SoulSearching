package com.github.enteraname74.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

internal data class ArtistWithMusics(
    @Embedded val artist: Artist = Artist(),
    @Relation(
        parentColumn = "artistId",
        entityColumn = "musicId",
        associateBy = Junction(MusicArtist::class)
    )
    val musics : List<Music> = emptyList()
)