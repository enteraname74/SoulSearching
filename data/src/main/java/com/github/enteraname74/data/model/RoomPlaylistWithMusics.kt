package com.github.enteraname74.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

internal data class RoomPlaylistWithMusics(
    @Embedded val roomPlaylist: RoomPlaylist = RoomPlaylist(),
    @Relation(
        parentColumn = "playlistId",
        entityColumn = "musicId",
        associateBy = Junction(RoomMusicPlaylist::class)
    )
    val roomMusics : List<RoomMusic> = emptyList()
) {
    fun toPlaylistWithMusicsNumber(): RoomPlaylistWithMusicsNumber {
        return RoomPlaylistWithMusicsNumber(
            roomPlaylist = roomPlaylist,
            musicsNumber = roomMusics.filter { !it.isHidden }.size
        )
    }
}