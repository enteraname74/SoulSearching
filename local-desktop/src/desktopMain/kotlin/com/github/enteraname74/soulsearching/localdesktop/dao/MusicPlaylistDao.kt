package com.github.enteraname74.soulsearching.localdesktop.dao

import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.exposedflows.flowTransactionOn
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicPlaylistTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.upsert

internal class MusicPlaylistDao {
    suspend fun upsert(musicPlaylist: MusicPlaylist) {
        flowTransactionOn {
            MusicPlaylistTable.upsert {
                it[id] = musicPlaylist.id
                it[musicId] = musicPlaylist.musicId
                it[playlistId] = musicPlaylist.playlistId
                it[dataMode] = musicPlaylist.dataMode.value
            }
        }
    }

    suspend fun upsertAll(musicPlaylists: List<MusicPlaylist>) {
        flowTransactionOn {
            MusicPlaylistTable.batchUpsert(musicPlaylists) { musicPlaylist ->
                this[MusicPlaylistTable.id] = musicPlaylist.id
                this[MusicPlaylistTable.musicId] = musicPlaylist.musicId
                this[MusicPlaylistTable.playlistId] = musicPlaylist.playlistId
                this[MusicPlaylistTable.dataMode] = musicPlaylist.dataMode.value
            }
        }
    }

    suspend fun delete(musicPlaylist: MusicPlaylist) {
        flowTransactionOn {
            MusicPlaylistTable.
            deleteWhere { id eq musicPlaylist.id }
        }
    }

    suspend fun deleteAll(musicPlaylistsIds: List<String>) {
        flowTransactionOn {
            MusicPlaylistTable.
            deleteWhere { id inList musicPlaylistsIds }
        }
    }

    suspend fun deleteAll(dataMode: String) {
        flowTransactionOn {
            MusicPlaylistTable.
            deleteWhere { MusicPlaylistTable.dataMode eq dataMode }
        }
    }
}