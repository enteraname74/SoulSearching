package com.github.enteraname74.soulsearching.localdesktop.tables

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Cover
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.util.*

/**
 * Table for storing Albums.
 */
internal object AlbumTable: UUIDTable() {
    val albumName = varchar("albumName", 128)
    val coverId = uuid("coverId").nullable()
    val addedDate = datetime("addedDate")
    val nbPlayed = integer("nbPlayed")
    val isInQuickAccess = bool("isInQuickAccess")
}

/**
 * Builds an Album from a ResultRow.
 */
internal fun ResultRow.toAlbum(): Album? =
    try {
        Album(
            albumId = this[AlbumTable.id].value,
            albumName = this[AlbumTable.albumName],
            cover = this[AlbumTable.coverId]?.let { coverId ->
                Cover.FileCover(
                    fileCoverId = coverId,
                )
            },
            addedDate = this[AlbumTable.addedDate],
            nbPlayed = this[AlbumTable.nbPlayed],
            isInQuickAccess = this[AlbumTable.isInQuickAccess],
        )
    } catch (_: Exception) {
        null
    }