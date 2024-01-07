package com.github.enteraname74.data.model

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
) {

    /**
     * Converts a RoomPlaylistWithMusics to a RoomPlaylistWithMusicsNumber.
     */
    fun toPlaylistWithMusicsNumber(): RoomPlaylistWithMusicsNumber {
        return RoomPlaylistWithMusicsNumber(
            roomPlaylist = roomPlaylist,
            musicsNumber = roomMusics.filter { !it.isHidden }.size
        )
    }
}

/**
 * Converts a RoomPlaylistWithMusics to a PlaylistWithMusics.
 */
internal fun RoomPlaylistWithMusics.toPlaylistWIthMusics(): PlaylistWithMusics = PlaylistWithMusics(
    playlist = roomPlaylist.toPlaylist(),
    musics = roomMusics.map { it.toMusic() }
)

/**
 * Converts a PlaylistWithMusics to a RoomPlaylistWithMusics.
 */
internal fun PlaylistWithMusics.toRoomPlaylistWithMusics(): RoomPlaylistWithMusics =
    RoomPlaylistWithMusics(
        roomPlaylist = playlist.toRoomPlaylist(),
        roomMusics = musics.map { it.toRoomMusic() }
    )