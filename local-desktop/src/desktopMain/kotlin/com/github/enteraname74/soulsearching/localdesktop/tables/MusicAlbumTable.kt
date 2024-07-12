package com.github.enteraname74.soulsearching.localdesktop.tables

import com.github.enteraname74.soulsearching.localdesktop.tables.MusicTable
import com.github.enteraname74.soulsearching.localdesktop.tables.AlbumTable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import java.util.UUID

/**
 * Table for storing MusicAlbums.
 */
internal object MusicAlbumTable : LongIdTable() {
    val musicId = reference("musicId", MusicTable.id, onDelete = ReferenceOption.CASCADE)
    val albumId = reference("albumId", AlbumTable.id, onDelete = ReferenceOption.CASCADE)
}