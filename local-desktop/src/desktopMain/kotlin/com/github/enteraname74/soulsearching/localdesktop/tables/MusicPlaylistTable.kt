package com.github.enteraname74.soulsearching.localdesktop.tables

import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicTable
import com.github.enteraname74.soulsearching.localdesktop.tables.PlaylistTable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import java.util.*

/**
 * Table for storing MusicPlaylists.
 */
internal object MusicPlaylistTable: LongIdTable() {
    val musicId = reference("musicId", MusicTable.id, onDelete = ReferenceOption.CASCADE)
    val playlistId = reference("playlistId", PlaylistTable.id, onDelete = ReferenceOption.CASCADE)
}

/**
 * Builds a MusicPlaylist from a ResultRow.
 */
internal fun ResultRow.toMusicPlaylist(): MusicPlaylist? =
    try {
        MusicPlaylist(
            id = this[MusicPlaylistTable.id].value,
            musicId = this[MusicPlaylistTable.musicId].value,
            playlistId = this[MusicPlaylistTable.playlistId].value
        )
    } catch (_: Exception) {
        null
    }