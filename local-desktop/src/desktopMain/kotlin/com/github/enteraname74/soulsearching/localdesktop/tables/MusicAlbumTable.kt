package com.github.enteraname74.soulsearching.localdesktop.tables

import com.github.enteraname74.domain.model.MusicAlbum
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow

/**
 * Table for storing MusicAlbums.
 */
internal object MusicAlbumTable : LongIdTable() {
    val musicId = reference("musicId", MusicTable.id, onDelete = ReferenceOption.CASCADE)
    val albumId = reference("albumId", AlbumTable.id, onDelete = ReferenceOption.CASCADE)
}

internal fun ResultRow.toMusicAlbum(): MusicAlbum? =
    try {
        MusicAlbum(
            musicId = this[MusicAlbumTable.musicId].value,
            albumId = this[MusicAlbumTable.albumId].value,
        )
    } catch (_: Exception) {
        null
    }