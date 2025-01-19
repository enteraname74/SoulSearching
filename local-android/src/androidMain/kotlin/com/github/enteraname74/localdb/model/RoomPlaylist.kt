package com.github.enteraname74.localdb.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.Playlist
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
    var isInQuickAccess: Boolean = false,
    var dataMode: String,
)

/**
 * Converts a RoomPlaylist to a Playlist.
 */
internal fun RoomPlaylist.toPlaylist(): Playlist = Playlist(
    playlistId = playlistId,
    name = name,
    cover = Cover.CoverFile(fileCoverId = coverId),
    isFavorite = isFavorite,
    addedDate = addedDate,
    nbPlayed = nbPlayed,
    isInQuickAccess = isInQuickAccess,
    dataMode = DataMode.fromString(
        dataMode
    ) ?: DataMode.Local,
)

/**
 * Converts a Playlist to a RoomPlaylist.
 */
internal fun Playlist.toRoomPlaylist(): RoomPlaylist = RoomPlaylist(
    playlistId = playlistId,
    name = name,
    coverId = (cover as? Cover.CoverFile)?.fileCoverId,
    isFavorite = isFavorite,
    addedDate = addedDate,
    nbPlayed = nbPlayed,
    isInQuickAccess = isInQuickAccess,
    dataMode = dataMode.value,
)