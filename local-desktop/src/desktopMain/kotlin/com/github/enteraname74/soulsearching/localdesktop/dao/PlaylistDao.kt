package com.github.enteraname74.soulsearching.localdesktop.dao

import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.exposedflows.asFlow
import com.github.enteraname74.exposedflows.flowTransactionOn
import com.github.enteraname74.exposedflows.mapResultRow
import com.github.enteraname74.exposedflows.mapSingleResultRow
import com.github.enteraname74.soulsearching.localdesktop.tables.*
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicPlaylistTable
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicTable
import com.github.enteraname74.soulsearching.localdesktop.tables.PlaylistTable
import com.github.enteraname74.soulsearching.localdesktop.tables.toMusic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

/**
 * Implementation of the PlaylistDao for Exposed.
 */
internal class PlaylistDao(
    private val musicDao: MusicDao,
) {
    suspend fun upsertAll(playlist: Playlist) {
        flowTransactionOn {
            PlaylistTable.upsert {
                it[id] = playlist.playlistId
                it[name] = playlist.name
                it[coverId] = (playlist.cover as? Cover.FileCover)?.fileCoverId
                it[isFavorite] = playlist.isFavorite
                it[addedDate] = playlist.addedDate
                it[nbPlayed] = playlist.nbPlayed
                it[isInQuickAccess] = playlist.isInQuickAccess
            }
        }
    }

    suspend fun upsertAll(playlists: List<Playlist>) {
        flowTransactionOn {
            PlaylistTable.batchUpsert(playlists) { playlist ->
                this[PlaylistTable.id] = playlist.playlistId
                this[PlaylistTable.name] = playlist.name
                this[PlaylistTable.coverId] = (playlist.cover as? Cover.FileCover)?.fileCoverId
                this[PlaylistTable.isFavorite] = playlist.isFavorite
                this[PlaylistTable.addedDate] = playlist.addedDate
                this[PlaylistTable.nbPlayed] = playlist.nbPlayed
                this[PlaylistTable.isInQuickAccess] = playlist.isInQuickAccess
            }
        }
    }

    suspend fun delete(playlist: Playlist) {
        flowTransactionOn {
            PlaylistTable.deleteWhere { id eq playlist.playlistId }
        }
    }

    fun getAll(): Flow<List<Playlist>> = transaction {
        PlaylistTable
            .selectAll()
            .orderBy(PlaylistTable.name to SortOrder.ASC)
            .asFlow()
            .mapResultRow { it.toPlaylist() }
            .map { it.filterNotNull() }
    }

    fun getAllPlaylistWithMusics(): Flow<List<PlaylistWithMusics>> = transaction {
        (PlaylistTable fullJoin MusicPlaylistTable fullJoin MusicTable)
            .selectAll()
            .asFlow()
            .map { list ->
                list.groupBy(
                    { it.toPlaylist() }, { it.toMusic() }
                ).map { (playlist,songs) ->
                    playlist?.let {
                        PlaylistWithMusics(
                            playlist = it,
                            musics = songs.filterNotNull(),
                        )
                    }
                }.filterNotNull()
            }
    }

    fun getFromId(playlistId: UUID): Flow<Playlist?> = transaction {
        PlaylistTable
            .selectAll()
            .where { PlaylistTable.id eq playlistId }
            .asFlow()
            .mapSingleResultRow { it.toPlaylist() }
    }

    fun getPlaylistWithMusics(playlistId: UUID): Flow<PlaylistWithMusics?> =
        combine(
            getFromId(playlistId),
            musicDao.getAllMusicFromPlaylist(playlistId)
        ) { playlist, songs ->
            playlist?.let {
                PlaylistWithMusics(
                    playlist = it,
                    musics = songs,
                )
            }
        }
}