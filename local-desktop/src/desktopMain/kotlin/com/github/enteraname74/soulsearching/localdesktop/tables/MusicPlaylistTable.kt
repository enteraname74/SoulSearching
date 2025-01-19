package com.github.enteraname74.soulsearching.localdesktop.tables

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.soulsearching.localdesktop.tables.AlbumTable.default
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicArtistTable.index
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicTable
import com.github.enteraname74.soulsearching.localdesktop.tables.PlaylistTable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import java.util.*

/**
 * Table for storing MusicPlaylists.
 */
internal object MusicPlaylistTable: Table() {
    val id = varchar("id", 256)
    val musicId = reference("musicId", MusicTable.id, onDelete = ReferenceOption.CASCADE).index(
        customIndexName = "index_MusicPlaylist_musicId",
    )
    val playlistId = reference("playlistId", PlaylistTable.id, onDelete = ReferenceOption.CASCADE).index(
        customIndexName = "index_MusicPlaylist_playlistId",
    )
    val dataMode = varchar("dataMode", 32).default(DataMode.Local.value)

    override val primaryKey: PrimaryKey = PrimaryKey(MusicArtistTable.id)
}

/**
 * Builds a MusicPlaylist from a ResultRow.
 */
internal fun ResultRow.toMusicPlaylist(): MusicPlaylist? =
    try {
        MusicPlaylist(
            musicId = this[MusicPlaylistTable.musicId].value,
            playlistId = this[MusicPlaylistTable.playlistId].value,
            dataMode = DataMode.fromString(
                this[MusicPlaylistTable.dataMode]
            ) ?: DataMode.Local
        )
    } catch (_: Exception) {
        null
    }