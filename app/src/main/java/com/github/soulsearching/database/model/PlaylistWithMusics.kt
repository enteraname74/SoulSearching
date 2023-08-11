package com.github.soulsearching.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class PlaylistWithMusics(
    @Embedded val playlist: Playlist = Playlist(),
    @Relation(
        parentColumn = "playlistId",
        entityColumn = "musicId",
        associateBy = Junction(MusicPlaylist::class)
    )
    val musics : List<Music> = emptyList()
)