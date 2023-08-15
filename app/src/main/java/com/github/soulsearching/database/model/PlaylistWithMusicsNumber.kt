package com.github.soulsearching.database.model

import androidx.room.Embedded

data class PlaylistWithMusicsNumber(
    @Embedded val playlist: Playlist = Playlist(),
    val musicsNumber : Int = 0
)
