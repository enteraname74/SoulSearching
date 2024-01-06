package com.github.enteraname74.data.model

import androidx.room.Embedded

internal data class RoomPlaylistWithMusicsNumber(
    @Embedded val roomPlaylist: RoomPlaylist = RoomPlaylist(),
    val musicsNumber : Int = 0
)
