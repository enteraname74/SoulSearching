package com.github.enteraname74.soulsearching.localdesktop.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

/**
 * Table for storing MusicAlbums.
 */
internal object MusicAlbumTable : LongIdTable() {
    val musicId = reference("musicId", MusicTable.id, onDelete = ReferenceOption.CASCADE)
    val albumId = reference("albumId", AlbumTable.id, onDelete = ReferenceOption.CASCADE)
}