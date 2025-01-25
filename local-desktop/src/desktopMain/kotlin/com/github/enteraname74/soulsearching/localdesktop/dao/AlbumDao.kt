package com.github.enteraname74.soulsearching.localdesktop.dao

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.exposedflows.asFlow
import com.github.enteraname74.exposedflows.flowTransactionOn
import com.github.enteraname74.exposedflows.mapResultRow
import com.github.enteraname74.exposedflows.mapSingleResultRow
import com.github.enteraname74.soulsearching.localdesktop.dbQuery
import com.github.enteraname74.soulsearching.localdesktop.tables.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
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
                it[coverId] = (album.cover as? Cover.CoverFile)?.fileCoverId
                it[addedDate] = album.addedDate
                it[nbPlayed] = album.nbPlayed
                it[isInQuickAccess] = album.isInQuickAccess
                it[artistId] = album.artistId
                it[dataMode] = album.dataMode.value
            }
        }
    }

    suspend fun upsertAll(albums: List<Album>) {
        flowTransactionOn {
            AlbumTable.batchUpsert(albums) {
                this[AlbumTable.id] = it.albumId
                this[AlbumTable.albumName] = it.albumName
                this[AlbumTable.coverId] = (it.cover as? Cover.CoverFile)?.fileCoverId
                this[AlbumTable.addedDate] = it.addedDate
                this[AlbumTable.nbPlayed] = it.nbPlayed
                this[AlbumTable.isInQuickAccess] = it.isInQuickAccess
                this[AlbumTable.artistId] = it.artistId
                this[AlbumTable.dataMode] = it.dataMode.value
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

    suspend fun deleteAll(dataMode: String) {
        flowTransactionOn {
            AlbumTable.deleteWhere { AlbumTable.dataMode eq dataMode }
        }
    }

    fun getAll(dataMode: String): Flow<List<Album>> = transaction {
        AlbumTable
            .selectAll()
            .where { AlbumTable.dataMode eq dataMode }
            .orderBy(AlbumTable.albumName to SortOrder.ASC)
            .asFlow()
            .mapResultRow { it.toAlbum() }
            .map { it.filterNotNull() }
    }

    suspend fun getAll(albumIds: List<UUID>): List<Album> = dbQuery {
        AlbumTable
            .selectAll()
            .where { AlbumTable.id inList albumIds }
            .mapNotNull { it.toAlbum() }
    }

    fun getAlbumsOfArtist(artistId: UUID): Flow<List<Album>> = transaction {
        AlbumTable
            .selectAll()
            .where { AlbumTable.artistId eq artistId }
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

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAlbumWithMusics(albumId: UUID): Flow<AlbumWithMusics?> = combine(
        getFromId(albumId),
        musicDao.getAllMusicFromAlbum(albumId),
        getFromId(albumId).flatMapLatest {
            it?.let {
                artistDao.getFromId(it.artistId)
            } ?: flowOf(null)
        }
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
        (AlbumTable fullJoin MusicTable fullJoin ArtistTable)
            .selectAll()
            .where { AlbumTable.artistId eq artistId }
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

    fun getAllAlbumsWithMusics(dataMode: String): Flow<List<AlbumWithMusics>> = transaction {
        (AlbumTable fullJoin MusicTable fullJoin ArtistTable)
            .selectAll()
            .where { AlbumTable.dataMode eq dataMode }
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