package com.github.enteraname74.localdesktop.daoimpl

import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.localdesktop.dao.MusicPlaylistDao
import com.github.enteraname74.localdesktop.dbQuery
import com.github.enteraname74.localdesktop.tables.MusicPlaylistTable
import com.github.enteraname74.localdesktop.utils.ExposedUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.upsert
import java.util.UUID

/**
 * Implementation of the MusicPlaylistDao for Exposed.
 */
class ExposedMusicPlaylistDaoImpl : MusicPlaylistDao {
    override suspend fun insertMusicIntoPlaylist(musicPlaylist: MusicPlaylist) {
        dbQuery {
            MusicPlaylistTable.upsert {
                if (musicPlaylist.id != 0L) it[id] = musicPlaylist.id
                it[musicId] = musicPlaylist.musicId.toString()
                it[playlistId] = musicPlaylist.playlistId.toString()
            }
        }
    }

    override suspend fun deleteMusicFromPlaylist(musicId: UUID, playlistId: UUID) {
        dbQuery {
            MusicPlaylistTable.deleteWhere { (MusicPlaylistTable.musicId eq musicId.toString()) and
                    (MusicPlaylistTable.playlistId eq playlistId.toString()) }
        }
    }

    override suspend fun getMusicPlaylist(musicId: UUID, playlistId: UUID): MusicPlaylist? = dbQuery {
        MusicPlaylistTable
            .selectAll()
            .where { (MusicPlaylistTable.musicId eq musicId.toString()) and
                    (MusicPlaylistTable.playlistId eq playlistId.toString()) }
            .map(ExposedUtils::resultRowToMusicPlaylist)
            .singleOrNull()
    }

    override suspend fun deleteMusicFromAllPlaylists(musicId: UUID) {
        dbQuery {
            MusicPlaylistTable.deleteWhere { MusicPlaylistTable.musicId eq musicId.toString() }
        }
    }
}