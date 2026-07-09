package com.github.enteraname74.localdb.model

import androidx.room3.Embedded
import androidx.room3.Junction
import androidx.room3.Relation
import com.github.enteraname74.domain.model.PlaylistWithMusics

/**
 * Room representation of a PlaylistWithMusics.
 */
data class RoomPlaylistWithMusics(
    @Embedded val roomPlaylist: RoomPlaylist,
    @Relation(
        parentColumns = ["playlistId"],
        entityColumns = ["musicId"],
        associateBy = Junction(RoomMusicPlaylist::class),
        entity = RoomMusic::class,
    )
    val roomMusics: List<RoomCompleteMusic>
)

/**
 * Converts a RoomPlaylistWithMusics to a PlaylistWithMusics.
 */
internal fun RoomPlaylistWithMusics.toPlaylistWithMusics(): PlaylistWithMusics = PlaylistWithMusics(
    playlist = roomPlaylist.toPlaylist(),
    musics = roomMusics.map { it.toMusic() }
)
