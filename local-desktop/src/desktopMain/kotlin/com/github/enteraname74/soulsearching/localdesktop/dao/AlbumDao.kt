package com.github.enteraname74.soulsearching.localdesktop.dao

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.exposedflows.asFlow
import com.github.enteraname74.exposedflows.flowTransactionOn
import com.github.enteraname74.exposedflows.mapResultRow
import com.github.enteraname74.exposedflows.mapSingleResultRow
import com.github.enteraname74.soulsearching.localdesktop.tables.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

internal class AlbumDao(
    private val musicDao: MusicDao,
    private val artistDao: ArtistDao,
) {
    suspend fun upsert(album: Album) {
        flowTransactionOn {
            AlbumTable.upsert {
                it[id] = album.albumId
                it[albumName] = album.albumName
                it[coverId] = (album.cover as? Cover.FileCover)?.fileCoverId
                it[addedDate] = album.addedDate
                it[nbPlayed] = album.nbPlayed
                it[isInQuickAccess] = album.isInQuickAccess
            }
        }
    }

    suspend fun upsertAll(albums: List<Album>) {
        flowTransactionOn {
            AlbumTable.batchUpsert(albums) {
                this[AlbumTable.id] = it.albumId
                this[AlbumTable.albumName] = it.albumName
                this[AlbumTable.coverId] = (it.cover as? Cover.FileCover)?.fileCoverId
                this[AlbumTable.addedDate] = it.addedDate
                this[AlbumTable.nbPlayed] = it.nbPlayed
                this[AlbumTable.isInQuickAccess] = it.isInQuickAccess
            }
        }
    }

    suspend fun delete(album: Album) {
        flowTransactionOn {
            AlbumTable.deleteWhere { id eq album.albumId }
        }
    }

    suspend fun deleteAll(ids: List<UUID>) {
        flowTransactionOn {
            AlbumTable.deleteWhere { Op.build { id inList ids } }
        }
    }

    fun getAll(): Flow<List<Album>> = transaction {
        AlbumTable
            .selectAll()
            .orderBy(AlbumTable.albumName to SortOrder.ASC)
            .asFlow()
            .mapResultRow { it.toAlbum() }
            .map { it.filterNotNull() }
    }

    fun getAlbumsOfArtist(artistId: UUID): Flow<List<Album>> = transaction {
        AlbumTable.join(
            otherTable = AlbumArtistTable,
            joinType = JoinType.INNER,
            onColumn = AlbumTable.id,
            otherColumn = AlbumArtistTable.albumId,
            additionalConstraint = { AlbumArtistTable.artistId eq artistId }
        ).selectAll()
            .asFlow()
            .mapResultRow { it.toAlbum() }
            .map { it.filterNotNull() }
    }

    fun getFromId(albumId: UUID): Flow<Album?> = transaction {
        AlbumTable
            .selectAll()
            .where { AlbumTable.id eq albumId }
            .asFlow()
            .mapSingleResultRow { it.toAlbum() }
    }

    fun getAlbumWithMusics(albumId: UUID): Flow<AlbumWithMusics?> = combine(
        getFromId(albumId),
        musicDao.getAllMusicFromAlbum(albumId),
        artistDao.getArtistOfAlbum(albumId)
    ) { album, songs, artist ->
        album?.let {
            AlbumWithMusics(
                album = it,
                musics = songs,
                artist = artist,
            )
        }
    }

    fun getAlbumsWithMusicsOfArtist(artistId: UUID): Flow<List<AlbumWithMusics>> = transaction {
        (AlbumTable.join(
            otherTable = AlbumArtistTable,
            joinType = JoinType.INNER,
            onColumn = AlbumTable.id,
            otherColumn = AlbumArtistTable.albumId,
            additionalConstraint = { AlbumArtistTable.artistId eq artistId }
        ) fullJoin MusicAlbumTable fullJoin MusicTable fullJoin ArtistTable)
            .selectAll()
            .asFlow()
            .map { list ->
                list.groupBy(
                    { it.toAlbum() }, { Pair(it.toMusic(), it.toArtist()) }
                ).map { (album, elt) ->
                    album?.let { actualAlbum ->
                        AlbumWithMusics(
                            album = actualAlbum,
                            artist = elt.map { it.second }.firstOrNull(),
                            musics = elt.filter { it.first != null }.map { it.first!! },
                        )
                    }
                }.filterNotNull()
            }
    }

    fun getAllAlbumsWithMusics(): Flow<List<AlbumWithMusics>> = transaction {
        (AlbumTable fullJoin MusicAlbumTable fullJoin MusicTable fullJoin AlbumArtistTable fullJoin ArtistTable)
            .selectAll()
            .asFlow()
            .map { list ->
                list.groupBy(
                    { it.toAlbum() }, { Pair(it.toMusic(), it.toArtist()) }
                ).map { (album, elt) ->
                    album?.let { actualAlbum ->
                        AlbumWithMusics(
                            album = actualAlbum,
                            artist = elt.map { it.second }.firstOrNull(),
                            musics = elt.filter { it.first != null }.map { it.first!! },
                        )
                    }
                }.filterNotNull()
            }
    }
}