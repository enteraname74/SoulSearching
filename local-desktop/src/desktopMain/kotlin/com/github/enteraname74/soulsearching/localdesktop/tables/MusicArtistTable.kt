package com.github.enteraname74.soulsearching.localdesktop.tables

import com.github.enteraname74.domain.model.MusicArtist
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

/**
 * Table for storing MusicArtists.
 */
internal object MusicArtistTable: Table() {
    val id = varchar("id", 256)
    val musicId = reference("musicId", MusicTable.id, onDelete = ReferenceOption.CASCADE).index(
        customIndexName = "index_MusicArtist_musicId",
    )
    val artistId = reference("artistId", ArtistTable.id, onDelete = ReferenceOption.CASCADE).index(
        customIndexName = "index_MusicArtist_artistId",
    )

    override val primaryKey: PrimaryKey = PrimaryKey(id)
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