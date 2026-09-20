package com.github.enteraname74.localdb.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Playlist
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
    var coverId: Uuid? = null,
    val coverUrl: String?,
    val isFavorite: Boolean = false,
    var addedDate: Instant = Clock.System.now(),
    var nbPlayed: Int = 0,
    var isInQuickAccess: Boolean = false,
    val lastUpdatedMillis: Long?,
)

/**
 * Converts a RoomPlaylist to a Playlist.
 */
internal fun RoomPlaylist.toPlaylist(): Playlist {
    val localCover = Cover.CoverFile(fileCoverId = coverId)
    val remoteCover = coverUrl?.let { Cover.Url(it, localCover) }

    val usedCover = if (remoteCover == null) {
        localCover
    } else {
        localCover.takeIf { !it.isEmpty() } ?: remoteCover
    }

    return Playlist(
        playlistId = playlistId,
        remoteId = remoteId,
        lastUpdatedMillis = lastUpdatedMillis,
        name = name,
        cover = usedCover,
        isFavorite = isFavorite,
        addedDate = addedDate,
        nbPlayed = nbPlayed,
        isInQuickAccess = isInQuickAccess,
    )
}

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
    remoteId = remoteId,
    coverUrl = (cover as? Cover.Url)?.url,
    lastUpdatedMillis = lastUpdatedMillis,
)
