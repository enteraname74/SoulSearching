package com.github.enteraname74.soulsearching.localdesktop.dao

import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.exposedflows.flowTransactionOn
import com.github.enteraname74.soulsearching.localdesktop.dbQuery
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicPlaylistTable
import com.github.enteraname74.soulsearching.localdesktop.tables.toMusicPlaylist
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.upsert
import java.util.*

internal class MusicPlaylistDao {
    suspend fun upsertMusicIntoPlaylist(musicPlaylist: MusicPlaylist) {
        flowTransactionOn {
            MusicPlaylistTable.upsert {
                it[id] = musicPlaylist.id
                it[musicId] = musicPlaylist.musicId
                it[playlistId] = musicPlaylist.playlistId
                it[dataMode] = musicPlaylist.dataMode.value
            }
        }
    }

    suspend fun deleteMusicFromPlaylist(musicId: UUID, playlistId: UUID) {
        flowTransactionOn {
            MusicPlaylistTable.
            deleteWhere { (MusicPlaylistTable.musicId eq musicId) and
                    (MusicPlaylistTable.playlistId eq playlistId) }
        }
    }

    suspend fun getMusicPlaylist(musicId: UUID, playlistId: UUID): MusicPlaylist? = dbQuery {
        MusicPlaylistTable
            .selectAll()
            .where { (MusicPlaylistTable.musicId eq musicId) and
                    (MusicPlaylistTable.playlistId eq playlistId) }
            .map { it.toMusicPlaylist() }
            .firstOrNull()
    }

    suspend fun deleteMusicFromAllPlaylists(musicId: UUID) {
        flowTransactionOn {
            MusicPlaylistTable.deleteWhere { MusicPlaylistTable.musicId eq musicId }
        }
    }
}