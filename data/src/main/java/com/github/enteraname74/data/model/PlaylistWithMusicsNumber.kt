package com.github.enteraname74.data.model

import androidx.room.Embedded

internal data class PlaylistWithMusicsNumber(
    @Embedded val playlist: Playlist = Playlist(),
    val musicsNumber : Int = 0
)
