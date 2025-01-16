package com.github.enteraname74.soulsearching.localdesktop.tables

import com.github.enteraname74.domain.model.MusicAlbum
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

/**
 * Table for storing MusicAlbums.
 */
internal object MusicAlbumTable : Table() {
    val id = varchar("id", 256)
    val musicId = reference("musicId", MusicTable.id, onDelete = ReferenceOption.CASCADE)
    val albumId = reference("albumId", AlbumTable.id, onDelete = ReferenceOption.CASCADE)

    override val primaryKey: PrimaryKey = PrimaryKey(MusicArtistTable.id)

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