package com.github.enteraname74.localdesktop.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import java.util.UUID

/**
 * Table for storing MusicAlbums.
 */
internal object MusicAlbumTable : LongIdTable() {
    val musicId = reference("musicId", MusicTable.musicId, onDelete = ReferenceOption.CASCADE)
    val albumId = reference("albumId", AlbumTable.albumId, onDelete = ReferenceOption.CASCADE)
}