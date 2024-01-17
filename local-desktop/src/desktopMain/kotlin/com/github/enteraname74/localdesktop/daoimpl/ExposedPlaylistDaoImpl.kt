package com.github.enteraname74.localdesktop.daoimpl

import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.localdesktop.dao.PlaylistDao
import com.github.enteraname74.localdesktop.dbQuery
import com.github.enteraname74.localdesktop.tables.PlaylistTable
import com.github.enteraname74.localdesktop.utils.ExposedUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.upsert
import java.util.UUID

/**
 * Implementation of the PlaylistDao for Exposed.
 */
class ExposedPlaylistDaoImpl: PlaylistDao {
    override suspend fun insertPlaylist(playlist: Playlist) {
        dbQuery {
            PlaylistTable.upsert {
                it[playlistId] = playlist.playlistId.toString()
                it[name] = playlist.name
                it[coverId] = playlist.coverId?.toString()
                it[isFavorite] = playlist.isFavorite
                it[addedDate] = playlist.addedDate
                it[nbPlayed] = playlist.nbPlayed
                it[isInQuickAccess] = playlist.isInQuickAccess
            }
        }
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        dbQuery {
            PlaylistTable.deleteWhere { playlistId eq playlist.playlistId.toString() }
        }
    }

    override fun getAllPlaylistsSortByNameAscAsFlow(): Flow<List<Playlist>> = transaction {
        flowOf(
            PlaylistTable
                .selectAll()
                .orderBy(PlaylistTable.name to SortOrder.ASC)
                .map(ExposedUtils::resultRowToPlaylist)
        )
    }

    override fun getAllPlaylistsWithMusicsSortByNameAscAsFlow(): Flow<List<PlaylistWithMusics>> {
        return flowOf(emptyList())
    }

    override fun getAllPlaylistWithMusicsSortByNameDescAsFlow(): Flow<List<PlaylistWithMusics>> {
        return flowOf(emptyList())
    }

    override fun getAllPlaylistWithMusicsSortByAddedDateAscAsFlow(): Flow<List<PlaylistWithMusics>> {
        return flowOf(emptyList())
    }

    override fun getAllPlaylistWithMusicsSortByAddedDateDescAsFlow(): Flow<List<PlaylistWithMusics>> {
        return flowOf(emptyList())
    }

    override fun getAllPlaylistWithMusicsSortByNbPlayedAscAsFlow(): Flow<List<PlaylistWithMusics>> {
        return flowOf(emptyList())
    }

    override fun getAllPlaylistWithMusicsSortByNbPlayedDescAsFlow(): Flow<List<PlaylistWithMusics>> {
        return flowOf(emptyList())
    }

    override fun getAllPlaylistsFromQuickAccessAsFlow(): Flow<List<PlaylistWithMusics>> {
        return flowOf(emptyList())
    }

    override suspend fun getFavoritePlaylist(): Playlist = dbQuery {
        PlaylistTable
            .selectAll()
            .where { PlaylistTable.isFavorite eq true }
            .map(ExposedUtils::resultRowToPlaylist)
            .single()
    }

    override suspend fun getPlaylistFromId(playlistId: UUID): Playlist = dbQuery {
        PlaylistTable
            .selectAll()
            .where { PlaylistTable.playlistId eq playlistId.toString() }
            .map(ExposedUtils::resultRowToPlaylist)
            .single()
    }

    override fun getPlaylistWithMusicsAsFlow(playlistId: UUID): Flow<PlaylistWithMusics?> {
        return flowOf(null)
    }

    override suspend fun getAllPlaylistsWithMusics(): List<PlaylistWithMusics> {
        return emptyList()
    }

    override suspend fun getNumberOfPlaylistsWithCoverId(coverId: UUID): Int = dbQuery {
        PlaylistTable
            .selectAll()
            .where { PlaylistTable.coverId eq coverId.toString() }
            .count()
            .toInt()
    }

    override suspend fun updateQuickAccessState(newQuickAccessState: Boolean, playlistId: UUID) {
        dbQuery {
            PlaylistTable.update({PlaylistTable.playlistId eq playlistId.toString()}) {
                it[isInQuickAccess] = newQuickAccessState
            }
        }
    }

    override suspend fun getNbPlayedOfPlaylist(playlistId: UUID): Int = dbQuery {
        val list = PlaylistTable
            .select(PlaylistTable.nbPlayed)
            .where { PlaylistTable.playlistId eq playlistId.toString() }
            .map { it[PlaylistTable.nbPlayed] }

        if (list.isEmpty()) 0 else list[0]
    }

    override suspend fun updateNbPlayed(newNbPlayed: Int, playlistId: UUID) {
        dbQuery {
            PlaylistTable.update({PlaylistTable.playlistId eq playlistId.toString()}) {
                it[nbPlayed] = newNbPlayed
            }
        }
    }
}