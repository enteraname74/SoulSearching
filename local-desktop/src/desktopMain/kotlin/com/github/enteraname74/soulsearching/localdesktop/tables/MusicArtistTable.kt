package com.github.enteraname74.soulsearching.localdesktop.tables

import com.github.enteraname74.domain.model.MusicArtist
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow

/**
 * Table for storing MusicArtists.
 */
internal object MusicArtistTable: LongIdTable() {
    val musicId = reference("musicId", MusicTable.id, onDelete = ReferenceOption.CASCADE)
    val artistId = reference("artistId", ArtistTable.id, onDelete = ReferenceOption.CASCADE)
}

internal fun ResultRow.toMusicArtist(): MusicArtist? =
    try {
        MusicArtist(
            musicId = this[MusicArtistTable.musicId].value,
            artistId = this[MusicArtistTable.artistId].value,
        )
    } catch (_: Exception) {
        null
    }