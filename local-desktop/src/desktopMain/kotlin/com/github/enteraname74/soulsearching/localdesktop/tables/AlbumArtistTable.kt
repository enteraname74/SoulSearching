package com.github.enteraname74.soulsearching.localdesktop.tables

import com.github.enteraname74.domain.model.AlbumArtist
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow

/**
 * Table for storing AlbumArtists.
 */
internal object AlbumArtistTable: LongIdTable() {
    val albumId = reference("albumId", AlbumTable.id, onDelete = ReferenceOption.CASCADE)
    val artistId = reference("artistId", ArtistTable.id, onDelete = ReferenceOption.CASCADE)
}

internal fun ResultRow.toAlbumArtist(): AlbumArtist = AlbumArtist(
    albumId = this[AlbumArtistTable.albumId].value,
    artistId = this[AlbumArtistTable.artistId].value,
)