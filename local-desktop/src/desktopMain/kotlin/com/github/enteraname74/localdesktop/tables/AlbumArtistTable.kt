package com.github.enteraname74.localdesktop.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

/**
 * Table for storing AlbumArtists.
 */
object AlbumArtistTable: LongIdTable() {
    val albumId = reference("albumId", AlbumTable.albumId, onDelete = ReferenceOption.CASCADE)
    val artistId = reference("artistId", ArtistTable.artistId, onDelete = ReferenceOption.CASCADE)
}