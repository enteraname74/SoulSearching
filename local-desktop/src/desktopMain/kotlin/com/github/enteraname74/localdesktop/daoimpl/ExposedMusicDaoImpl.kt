package com.github.enteraname74.localdesktop.daoimpl

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.localdesktop.dao.MusicDao
import com.github.enteraname74.localdesktop.dbQuery
import com.github.enteraname74.localdesktop.tables.MusicAlbumTable
import com.github.enteraname74.localdesktop.tables.MusicPlaylistTable
import com.github.enteraname74.localdesktop.tables.MusicTable
import com.github.enteraname74.localdesktop.tables.PlaylistTable
import com.github.enteraname74.localdesktop.utils.ExposedUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.upsert
import java.util.UUID

/**
 * Implementation of the MusicDao for Exposed.
 */
class ExposedMusicDaoImpl : MusicDao {
    override suspend fun insertMusic(music: Music) {
        dbQuery {
            MusicTable.upsert {
                it[musicId] = music.musicId.toString()
                it[name] = music.name
                it[album] = music.album
                it[artist] = music.artist
                it[coverId] = music.coverId?.toString()
                it[duration] = music.duration
                it[path] = music.path
                it[folder] = music.folder
                it[addedDate] = music.addedDate
                it[nbPlayed] = music.nbPlayed
                it[isInQuickAccess] = music.isInQuickAccess
                it[isHidden] = music.isHidden
            }
        }
    }

    override suspend fun deleteMusic(music: Music) {
        dbQuery {
            MusicTable.deleteWhere { musicId eq music.musicId.toString() }
        }
    }

    override suspend fun deleteMusicFromAlbum(album: String, artist: String) {
        dbQuery {
            MusicTable
                .deleteWhere { (MusicTable.album eq album) and (MusicTable.artist eq artist) }
        }
    }

    override suspend fun getMusicFromPath(musicPath: String): Music? = dbQuery {
        MusicTable
            .selectAll()
            .where { MusicTable.path eq musicPath }
            .map(ExposedUtils::resultRowToMusic)
            .singleOrNull()
    }

    override suspend fun getMusicFromId(musicId: UUID): Music = dbQuery {
        MusicTable
            .selectAll()
            .where { MusicTable.musicId eq musicId.toString() }
            .map(ExposedUtils::resultRowToMusic)
            .single()
    }

    override suspend fun getMusicFromFavoritePlaylist(musicId: UUID): Music? = dbQuery {
        MusicPlaylistTable
            .join(
                otherTable = MusicTable,
                joinType = JoinType.INNER,
                onColumn = MusicPlaylistTable.musicId,
                otherColumn = MusicTable.musicId,
                additionalConstraint = {
                    MusicTable.musicId eq musicId.toString()
                }
            ).join(
                otherTable = PlaylistTable,
                joinType = JoinType.INNER,
                onColumn = MusicPlaylistTable.playlistId,
                otherColumn = PlaylistTable.playlistId,
                additionalConstraint = {
                    PlaylistTable.isFavorite eq true
                }
            ).selectAll()
            .map(ExposedUtils::resultRowToMusic)
            .singleOrNull()
    }

    override suspend fun getAllMusicsPaths(): List<String> = dbQuery {
        MusicTable
            .select(MusicTable.path)
            .map { it.toString() }
    }

    override fun getAllMusicsSortByNameAscAsFlow(): Flow<List<Music>> = transaction {
        flowOf(
            MusicTable
                .selectAll()
                .orderBy(MusicTable.name to SortOrder.ASC)
                .map(ExposedUtils::resultRowToMusic)
        )
    }

    override fun getAllMusicsSortByNameDescAsFlow(): Flow<List<Music>> = transaction {
        flowOf(
            MusicTable
                .selectAll()
                .orderBy(MusicTable.name to SortOrder.DESC)
                .map(ExposedUtils::resultRowToMusic)
        )
    }

    override fun getAllMusicsSortByAddedDateAscAsFlow(): Flow<List<Music>> = transaction {
        flowOf(
            MusicTable
                .selectAll()
                .orderBy(MusicTable.addedDate to SortOrder.ASC)
                .map(ExposedUtils::resultRowToMusic)
        )
    }

    override fun getAllMusicsSortByAddedDateDescAsFlow(): Flow<List<Music>> = transaction {
        flowOf(
            MusicTable
                .selectAll()
                .orderBy(MusicTable.addedDate to SortOrder.DESC)
                .map(ExposedUtils::resultRowToMusic)
        )
    }

    override fun getAllMusicsSortByNbPlayedAscAsFlow(): Flow<List<Music>> = transaction {
        flowOf(
            MusicTable
                .selectAll()
                .orderBy(MusicTable.nbPlayed to SortOrder.ASC)
                .map(ExposedUtils::resultRowToMusic)
        )
    }

    override fun getAllMusicsSortByNbPlayedDescAsFlow(): Flow<List<Music>> = transaction {
        flowOf(
            MusicTable
                .selectAll()
                .orderBy(MusicTable.nbPlayed to SortOrder.DESC)
                .map(ExposedUtils::resultRowToMusic)
        )
    }

    override fun getAllMusicsFromQuickAccessAsFlow(): Flow<List<Music>> = transaction {
        flowOf(
            MusicTable
                .selectAll()
                .where { MusicTable.isInQuickAccess eq true }
                .map(ExposedUtils::resultRowToMusic)
        )
    }

    override suspend fun getAllMusicFromAlbum(albumId: UUID): List<Music> = dbQuery {
        MusicTable.join(
            otherTable = MusicAlbumTable,
            joinType = JoinType.INNER,
            onColumn = MusicTable.musicId,
            otherColumn = MusicAlbumTable.musicId,
            additionalConstraint = { (MusicAlbumTable.albumId eq albumId.toString()) and (MusicTable.isHidden eq false) }
        )
            .selectAll()
            .map(ExposedUtils::resultRowToMusic)
    }

    override suspend fun getNumberOfMusicsWithCoverId(coverId: UUID): Int = dbQuery {
        MusicTable
            .selectAll()
            .where { MusicTable.coverId eq coverId.toString() }
            .count()
            .toInt()
    }

    override suspend fun updateQuickAccessState(newQuickAccessState: Boolean, musicId: UUID) {
        dbQuery {
            MusicTable.update({MusicTable.musicId eq musicId.toString()}) {
                it[isInQuickAccess] = newQuickAccessState
            }
        }
    }

    override suspend fun getNbPlayedOfMusic(musicId: UUID): Int = dbQuery {
        val list = MusicTable
            .select(MusicTable.nbPlayed)
            .where { MusicTable.musicId eq musicId.toString() }
            .map { it[MusicTable.nbPlayed] }

        if (list.isEmpty()) 0 else list[0]
    }

    override suspend fun updateNbPlayed(newNbPlayed: Int, musicId: UUID) {
        dbQuery {
            MusicTable.update({MusicTable.musicId eq musicId.toString()}) {
                it[nbPlayed] = nbPlayed
            }
        }
    }

    override suspend fun updateMusicsHiddenState(folderName: String, newIsHidden: Boolean) {
        dbQuery {
            MusicTable.update({MusicTable.folder eq folderName}) {
                it[isHidden] = newIsHidden
            }
        }
    }

    override suspend fun getMusicsFromFolder(folderName: String): List<Music> = dbQuery {
        MusicTable
            .selectAll()
            .where { MusicTable.folder eq folderName }
            .map(ExposedUtils::resultRowToMusic)
    }
}