package com.github.enteraname74.localandroid.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.enteraname74.model.Playlist
import java.time.LocalDateTime
import java.util.*

/**
 * Room representation of a Playlist.
 */
@Entity
internal data class RoomPlaylist(
    @PrimaryKey
    val playlistId: UUID = UUID.randomUUID(),
    var name: String = "",
    var coverId: UUID? = null,
    val isFavorite: Boolean = false,
    var addedDate: LocalDateTime = LocalDateTime.now(),
    var nbPlayed: Int = 0,
    var isInQuickAccess: Boolean = false
)

/**
 * Converts a RoomPlaylist to a Playlist.
 */
internal fun RoomPlaylist.toPlaylist(): Playlist = Playlist(
    playlistId = playlistId,
    name = name,
    coverId = coverId,
    isFavorite = isFavorite,
    addedDate = addedDate,
    nbPlayed = nbPlayed,
    isInQuickAccess = isInQuickAccess
)

/**
 * Converts a Playlist to a RoomPlaylist.
 */
internal fun Playlist.toRoomPlaylist(): RoomPlaylist = RoomPlaylist(
    playlistId = playlistId,
    name = name,
    coverId = coverId,
    isFavorite = isFavorite,
    addedDate = addedDate,
    nbPlayed = nbPlayed,
    isInQuickAccess = isInQuickAccess
)