package com.github.soulsearching.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class AlbumWithMusics(
    @Embedded val album: Album,
    @Relation(
        parentColumn = "albumId",
        entityColumn = "musicId",
        associateBy = Junction(MusicAlbum::class)
    )
    val musics : List<Music>
)