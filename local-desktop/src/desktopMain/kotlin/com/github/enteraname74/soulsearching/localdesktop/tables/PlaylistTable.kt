package com.github.enteraname74.soulsearching.localdesktop.tables

import com.github.enteraname74.domain.model.Playlist
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.util.*

/**
 * Table for storing Playlists.
 */
internal object PlaylistTable: UUIDTable() {
    var name = varchar("name", 128)
    var coverId = varchar("coverId", 128).nullable()
    val isFavorite = bool("isFavorite")
    var addedDate = datetime("addedDate")
    var nbPlayed = integer("nbPlayed")
    var isInQuickAccess = bool("isInQuickAccess")
}

/**
 * Builds a Playlist from a ResultRow.
 */
internal fun ResultRow.toPlaylist(): Playlist = Playlist(
    playlistId = this[PlaylistTable.id].value,
    name = this[PlaylistTable.name],
    coverId = this[PlaylistTable.coverId]?.let { UUID.fromString(it) },
    isFavorite = this[PlaylistTable.isFavorite],
    addedDate = this[PlaylistTable.addedDate],
    nbPlayed = this[PlaylistTable.nbPlayed],
    isInQuickAccess = this[PlaylistTable.isInQuickAccess]
)