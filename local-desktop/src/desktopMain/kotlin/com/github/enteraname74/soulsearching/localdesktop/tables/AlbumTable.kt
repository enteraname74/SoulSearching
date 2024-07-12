package com.github.enteraname74.soulsearching.localdesktop.tables

import com.github.enteraname74.domain.model.Album
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
    val coverId = varchar("coverId", 128).nullable()
    val addedDate = datetime("addedDate")
    val nbPlayed = integer("nbPlayed")
    val isInQuickAccess = bool("isInQuickAccess")
}

/**
 * Builds an Album from a ResultRow.
 */
internal fun ResultRow.toAlbum(): Album = Album(
    albumId = this[AlbumTable.id].value,
    albumName = this[AlbumTable.albumName],
    coverId = this[AlbumTable.coverId]?.let { UUID.fromString(it) },
    addedDate = this[AlbumTable.addedDate],
    nbPlayed = this[AlbumTable.nbPlayed],
    isInQuickAccess = this[AlbumTable.isInQuickAccess],
)