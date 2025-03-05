package com.github.enteraname74.soulsearching.localdesktop.tables

import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Playlist
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.datetime

/**
 * Table for storing Playlists.
 */
internal object PlaylistTable: UUIDTable() {
    var name = varchar("name", 128)
    var coverId = uuid("coverId").nullable()
    val isFavorite = bool("isFavorite")
    var addedDate = datetime("addedDate")
    var nbPlayed = integer("nbPlayed")
    var isInQuickAccess = bool("isInQuickAccess")
}

/**
 * Builds a Playlist from a ResultRow.
 */
internal fun ResultRow.toPlaylist(): Playlist? =
    try {
        Playlist(
            playlistId = this[PlaylistTable.id].value,
            name = this[PlaylistTable.name],
            cover = this[PlaylistTable.coverId]?.let { coverId ->
                Cover.CoverFile(
                    fileCoverId = coverId,
                )
            },
            isFavorite = this[PlaylistTable.isFavorite],
            addedDate = this[PlaylistTable.addedDate],
            nbPlayed = this[PlaylistTable.nbPlayed],
            isInQuickAccess = this[PlaylistTable.isInQuickAccess]
        )
    } catch (_: Exception) {
        null
    }