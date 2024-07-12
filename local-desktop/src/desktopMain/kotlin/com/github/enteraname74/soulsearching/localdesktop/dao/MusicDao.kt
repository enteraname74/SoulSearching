package com.github.enteraname74.soulsearching.localdesktop.dao

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.exposedflows.asFlow
import com.github.enteraname74.exposedflows.flowTransactionOn
import com.github.enteraname74.exposedflows.mapResultRow
import com.github.enteraname74.exposedflows.mapSingleResultRow
import com.github.enteraname74.soulsearching.localdesktop.dbQuery
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicAlbumTable
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicArtistTable
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicTable
import com.github.enteraname74.soulsearching.localdesktop.tables.toMusic
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicPlaylistTable
import kotlinx.coroutines.flow.Flow
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

internal class MusicDao {
    suspend fun upsert(music: Music) {
        flowTransactionOn {
            MusicTable.upsert {
                it[id] = music.musicId
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

    suspend fun delete(music: Music) {
        flowTransactionOn {
            MusicTable.deleteWhere { id eq music.musicId }
        }
    }

    suspend fun deleteMusicFromAlbum(album: String, artist: String) {
        flowTransactionOn {
            MusicTable
                .deleteWhere { (MusicTable.album eq album) and (MusicTable.artist eq artist) }
        }
    }

    suspend fun getFromPath(musicPath: String): Music? = dbQuery {
        MusicTable
            .selectAll()
            .where { MusicTable.path eq musicPath }
            .map { it.toMusic() }
            .firstOrNull()
    }

    fun getFromId(musicId: UUID): Flow<Music?> = transaction {
        MusicTable
            .selectAll()
            .where { MusicTable.id eq musicId }
            .asFlow()
            .mapSingleResultRow { it.toMusic() }
    }

    fun getAll(): Flow<List<Music>> = transaction {
        MusicTable
            .selectAll()
            .orderBy(MusicTable.name to SortOrder.ASC)
            .asFlow()
            .mapResultRow { it.toMusic() }
    }

    fun getAllMusicFromAlbum(albumId: UUID): Flow<List<Music>> = transaction {
        MusicTable.join(
            otherTable = MusicAlbumTable,
            joinType = JoinType.INNER,
            onColumn = MusicTable.id,
            otherColumn = MusicAlbumTable.musicId,
            additionalConstraint = { (MusicAlbumTable.albumId eq albumId) and (MusicTable.isHidden eq false) }
        )
            .selectAll()
            .asFlow()
            .mapResultRow { it.toMusic() }
    }

    fun getAllMusicFromArtist(artistId: UUID): Flow<List<Music>> = transaction {
        MusicTable.join(
            otherTable = MusicArtistTable,
            joinType = JoinType.INNER,
            onColumn = MusicTable.id,
            otherColumn = MusicArtistTable.musicId,
            additionalConstraint = { (MusicArtistTable.artistId eq artistId) and (MusicTable.isHidden eq false) }
        )
            .selectAll()
            .asFlow()
            .mapResultRow { it.toMusic() }
    }

    fun getAllMusicFromPlaylist(playlistId: UUID): Flow<List<Music>> = transaction {
        MusicTable.join(
            otherTable = MusicPlaylistTable,
            joinType = JoinType.INNER,
            onColumn = MusicTable.id,
            otherColumn = MusicPlaylistTable.musicId,
            additionalConstraint = { (MusicPlaylistTable.playlistId eq playlistId) and (MusicTable.isHidden eq false) }
        )
            .selectAll()
            .asFlow()
            .mapResultRow { it.toMusic() }
    }
}