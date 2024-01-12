package com.github.enteraname74.localandroid.model

import androidx.room.Embedded
import com.github.enteraname74.domain.model.PlaylistWithMusicsNumber

/**
 * Room representation of a PlaylistWithMusicsNumber.
 */
internal data class RoomPlaylistWithMusicsNumber(
    @Embedded val roomPlaylist: RoomPlaylist = RoomPlaylist(),
    val musicsNumber : Int = 0
)

/**
 * Converts a RoomPlaylistWithMusicsNumber to a PlaylistWithMusicsNumber.
 */
internal fun RoomPlaylistWithMusicsNumber.toPlaylistWithMusicsNumber(): PlaylistWithMusicsNumber = PlaylistWithMusicsNumber(
    playlist = roomPlaylist.toPlaylist(),
    musicsNumber = musicsNumber
)

/**
 * Converts a PlaylistWithMusicsNumber to a RoomPlaylistWithMusicsNumber.
 */
internal fun PlaylistWithMusicsNumber.toRoomPlaylistWithNumber(): RoomPlaylistWithMusicsNumber = RoomPlaylistWithMusicsNumber(
    roomPlaylist = playlist.toRoomPlaylist(),
    musicsNumber = musicsNumber
)
