package com.github.enteraname74.localdb.model

import androidx.room3.Embedded
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.github.enteraname74.localdb.model.cover.RoomCover
import com.github.enteraname74.localdb.model.cover.toRoomCover
import com.github.enteraname74.soulsearching.domain.model.Playlist
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid

/**
 * Room representation of a Playlist.
 */
@Entity
data class RoomPlaylist(
    @PrimaryKey
    val playlistId: Uuid = Uuid.random(),
    val remoteId: Uuid?,
    var name: String = "",
    @Embedded("cover_") val cover: RoomCover?,
    val isFavorite: Boolean = false,
    var addedDate: Instant = Clock.System.now(),
    var nbPlayed: Int = 0,
    var isInQuickAccess: Boolean = false,
    val lastUpdatedMillis: Long?,
)

/**
 * Converts a RoomPlaylist to a Playlist.
 */
internal fun RoomPlaylist.toPlaylist(): Playlist =
    Playlist(
        playlistId = playlistId,
        remoteId = remoteId,
        lastUpdatedMillis = lastUpdatedMillis,
        name = name,
        cover = cover?.toCover(null),
        isFavorite = isFavorite,
        addedDate = addedDate,
        nbPlayed = nbPlayed,
        isInQuickAccess = isInQuickAccess,
    )

/**
 * Converts a Playlist to a RoomPlaylist.
 */
internal fun Playlist.toRoomPlaylist(): RoomPlaylist = RoomPlaylist(
    playlistId = playlistId,
    name = name,
    isFavorite = isFavorite,
    addedDate = addedDate,
    nbPlayed = nbPlayed,
    isInQuickAccess = isInQuickAccess,
    remoteId = remoteId,
    lastUpdatedMillis = lastUpdatedMillis,
    cover = cover?.toRoomCover(),
)
