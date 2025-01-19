package com.github.enteraname74.soulsearching.localdesktop.tables

import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicTable.default
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.datetime

/**
 * Table for storing Playlists.
 */
internal object PlaylistTable: UUIDTable() {
    val name = varchar("name", 128)
    val coverId = uuid("coverId").nullable()
    val isFavorite = bool("isFavorite")
    val addedDate = datetime("addedDate")
    val nbPlayed = integer("nbPlayed")
    val isInQuickAccess = bool("isInQuickAccess")
    val dataMode = varchar("dataMode", 32).default(DataMode.Local.value)
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
            isInQuickAccess = this[PlaylistTable.isInQuickAccess],
            dataMode = DataMode.fromString(
                this[PlaylistTable.dataMode]
            ) ?: DataMode.Local,
        )
    } catch (_: Exception) {
        null
    }