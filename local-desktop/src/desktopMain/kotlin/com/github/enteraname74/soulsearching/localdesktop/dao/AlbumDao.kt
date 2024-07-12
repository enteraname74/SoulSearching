package com.github.enteraname74.soulsearching.localdesktop.dao

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.exposedflows.asFlow
import com.github.enteraname74.exposedflows.flowTransactionOn
import com.github.enteraname74.exposedflows.mapResultRow
import com.github.enteraname74.exposedflows.mapSingleResultRow
import com.github.enteraname74.localdesktop.tables.*
import com.github.enteraname74.soulsearching.localdesktop.tables.*
import com.github.enteraname74.soulsearching.localdesktop.tables.AlbumArtistTable
import com.github.enteraname74.soulsearching.localdesktop.tables.AlbumTable
import com.github.enteraname74.soulsearching.localdesktop.tables.ArtistTable
import com.github.enteraname74.soulsearching.localdesktop.tables.toAlbum
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
                it[coverId] = album.coverId?.toString()
                it[addedDate] = album.addedDate
                it[nbPlayed] = album.nbPlayed
                it[isInQuickAccess] = album.isInQuickAccess
            }
        }
    }

    suspend fun delete(album: Album) {
        flowTransactionOn {
            AlbumTable.deleteWhere { AlbumTable.id eq album.albumId }
        }
    }

    fun getAll(): Flow<List<Album>> = transaction {
        AlbumTable
            .selectAll()
            .orderBy(AlbumTable.albumName to SortOrder.ASC)
            .asFlow()
            .mapResultRow { it.toAlbum() }
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

    fun getAllAlbumsWithArtist(): Flow<List<AlbumWithArtist>> = transaction {
        (AlbumTable fullJoin AlbumArtistTable fullJoin ArtistTable)
            .selectAll()
            .asFlow()
            .map { list ->
                list.groupBy(
                    { it.toAlbum() }, { it.toArtist() }
                ).map { (k,v) ->
                    AlbumWithArtist(
                        album = k,
                        artist = v.firstOrNull(),
                    )
                }
            }
    }

    fun getAllAlbumsWithMusics(): Flow<List<AlbumWithMusics>> = transaction {
        (AlbumTable fullJoin MusicAlbumTable fullJoin MusicTable fullJoin AlbumArtistTable fullJoin ArtistTable)
            .selectAll()
            .asFlow()
            .map { list ->
                list.groupBy(
                    { it.toAlbum() }, { Pair(it.toMusic(), it.toArtist()) }
                ).map { (k,v) ->
                    AlbumWithMusics(
                        album = k,
                        artist = v.map { it.second }.firstOrNull(),
                        musics = v.map { it.first },
                    )
                }
            }
    }
}