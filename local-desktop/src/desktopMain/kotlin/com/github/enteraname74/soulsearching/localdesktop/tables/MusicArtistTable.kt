package com.github.enteraname74.soulsearching.localdesktop.tables

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicTable.default
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
    val dataMode = varchar("dataMode", 32).default(DataMode.Local.value)

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

internal fun ResultRow.toMusicArtist(): MusicArtist? =
    try {
        MusicArtist(
            musicId = this[MusicArtistTable.musicId].value,
            artistId = this[MusicArtistTable.artistId].value,
            dataMode = DataMode.fromString(
                this[MusicArtistTable.dataMode]
            ) ?: DataMode.Local,
        )
    } catch (_: Exception) {
        null
    }