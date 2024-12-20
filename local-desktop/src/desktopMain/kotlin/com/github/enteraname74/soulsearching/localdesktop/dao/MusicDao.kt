package com.github.enteraname74.soulsearching.localdesktop.dao

import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.exposedflows.asFlow
import com.github.enteraname74.exposedflows.flowTransactionOn
import com.github.enteraname74.exposedflows.mapResultRow
import com.github.enteraname74.exposedflows.mapSingleResultRow
import com.github.enteraname74.soulsearching.localdesktop.dbQuery
import com.github.enteraname74.soulsearching.localdesktop.tables.*
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicTable.addedDate
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicTable.album
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicTable.artist
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicTable.coverId
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicTable.duration
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicTable.folder
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicTable.isHidden
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicTable.isInQuickAccess
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicTable.nbPlayed
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicTable.path
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

internal class MusicDao {
    suspend fun upsert(music: Music) {
        flowTransactionOn(MusicTable) {
            MusicTable.upsert {
                it[id] = music.musicId
                it[name] = music.name
                it[album] = music.album
                it[artist] = music.artist
                it[coverId] = (music.cover as? Cover.CoverFile)?.fileCoverId
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

    suspend fun upsertAll(musics: List<Music>) {
        flowTransactionOn {
            MusicTable.batchUpsert(musics) {music ->
                this[MusicTable.id] = music.musicId
                this[MusicTable.name] = music.name
                this[album] = music.album
                this[artist] = music.artist
                this[coverId] = (music.cover as? Cover.CoverFile)?.fileCoverId
                this[duration] = music.duration
                this[path] = music.path
                this[folder] = music.folder
                this[addedDate] = music.addedDate
                this[nbPlayed] = music.nbPlayed
                this[isInQuickAccess] = music.isInQuickAccess
                this[isHidden] = music.isHidden
            }
        }
    }

    suspend fun delete(music: Music) {
        flowTransactionOn {
            MusicTable.deleteWhere { id eq music.musicId }
        }
    }

    suspend fun deleteAll(ids: List<UUID>) {
        flowTransactionOn {
            MusicTable.deleteWhere { Op.build { id inList ids } }
        }
    }

    suspend fun getFromPath(musicPath: String): Music? = dbQuery {
        MusicTable
            .selectAll()
            .where { path eq musicPath }
            .firstOrNull()
            ?.toMusic()
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
            .map { it.filterNotNull() }
    }

    fun getAllMusicFromAlbum(albumId: UUID): Flow<List<Music>> = transaction {
        MusicTable.join(
            otherTable = MusicAlbumTable,
            joinType = JoinType.INNER,
            onColumn = MusicTable.id,
            otherColumn = MusicAlbumTable.musicId,
            additionalConstraint = { (MusicAlbumTable.albumId eq albumId) and (isHidden eq false) }
        )
            .selectAll()
            .asFlow()
            .mapResultRow { it.toMusic() }
            .map { it.filterNotNull() }
    }

    fun getAllMusicFromArtist(artistId: UUID): Flow<List<Music>> = transaction {
        MusicTable.join(
            otherTable = MusicArtistTable,
            joinType = JoinType.INNER,
            onColumn = MusicTable.id,
            otherColumn = MusicArtistTable.musicId,
            additionalConstraint = { (MusicArtistTable.artistId eq artistId) and (isHidden eq false) }
        )
            .selectAll()
            .asFlow()
            .mapResultRow { it.toMusic() }
            .map { it.filterNotNull() }
    }

    fun getAllMusicFromPlaylist(playlistId: UUID): Flow<List<Music>> = transaction {
        MusicTable.join(
            otherTable = MusicPlaylistTable,
            joinType = JoinType.INNER,
            onColumn = MusicTable.id,
            otherColumn = MusicPlaylistTable.musicId,
            additionalConstraint = { (MusicPlaylistTable.playlistId eq playlistId) and (isHidden eq false) }
        )
            .selectAll()
            .asFlow()
            .mapResultRow { it.toMusic() }
            .map { it.filterNotNull() }
    }
}