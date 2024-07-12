package com.github.enteraname74.soulsearching.localdesktop.tables

import com.github.enteraname74.soulsearching.localdesktop.tables.MusicTable
import com.github.enteraname74.soulsearching.localdesktop.tables.ArtistTable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

/**
 * Table for storing MusicArtists.
 */
internal object MusicArtistTable: LongIdTable() {
    val musicId = reference("musicId", MusicTable.id, onDelete = ReferenceOption.CASCADE)
    val artistId = reference("artistId", ArtistTable.id, onDelete = ReferenceOption.CASCADE)
}