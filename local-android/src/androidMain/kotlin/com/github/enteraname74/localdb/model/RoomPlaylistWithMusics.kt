package com.github.enteraname74.localdb.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.github.enteraname74.domain.model.PlaylistWithMusics

/**
 * Room representation of a PlaylistWithMusics.
 */
internal data class RoomPlaylistWithMusics(
    @Embedded val roomPlaylist: RoomPlaylist = RoomPlaylist(),
    @Relation(
        parentColumn = "playlistId",
        entityColumn = "musicId",
        associateBy = Junction(RoomMusicPlaylist::class)
    )
    val roomMusics: List<RoomMusic> = emptyList()
)

/**
 * Converts a RoomPlaylistWithMusics to a PlaylistWithMusics.
 */
internal fun RoomPlaylistWithMusics.toPlaylistWIthMusics(): PlaylistWithMusics = PlaylistWithMusics(
    playlist = roomPlaylist.toPlaylist(),
    musics = roomMusics.map { it.toMusic() }
)