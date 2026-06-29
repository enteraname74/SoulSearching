package com.github.enteraname74.localdb.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Playlist
import java.time.LocalDateTime
import java.util.UUID
import kotlin.uuid.Uuid

/**
 * Room representation of a Playlist.
 */
@Entity
data class RoomPlaylist(
    @PrimaryKey
    val playlistId: UUID = UUID.randomUUID(),
    val remoteId: Uuid?,
    var name: String = "",
    var coverId: UUID? = null,
    val coverUrl: String?,
    val isFavorite: Boolean = false,
    var addedDate: LocalDateTime = LocalDateTime.now(),
    var nbPlayed: Int = 0,
    var isInQuickAccess: Boolean = false,
    val lastUpdatedMillis: Long?,
)

/**
 * Converts a RoomPlaylist to a Playlist.
 */
internal fun RoomPlaylist.toPlaylist(): Playlist {
    val localCover = Cover.CoverFile(fileCoverId = coverId)
    val remoteCover = coverUrl?.let { Cover.Url(it) }

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