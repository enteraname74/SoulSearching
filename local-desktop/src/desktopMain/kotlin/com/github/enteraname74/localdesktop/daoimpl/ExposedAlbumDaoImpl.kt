package com.github.enteraname74.localdesktop.daoimpl

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.localdesktop.dao.AlbumDao
import com.github.enteraname74.localdesktop.dbQuery
import com.github.enteraname74.localdesktop.tables.AlbumArtistTable
import com.github.enteraname74.localdesktop.tables.AlbumTable
import com.github.enteraname74.localdesktop.utils.ExposedUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.upsert
import java.util.UUID

/**
 * Implementation of the AlbumDao for Exposed.
 */
class ExposedAlbumDaoImpl: AlbumDao {
    override suspend fun insertAlbum(album: Album) {
        dbQuery {
            AlbumTable.upsert {
                it[albumId] = album.albumId.toString()
                it[albumName] = album.albumName
                it[coverId] = album.coverId?.toString()
                it[addedDate] = album.addedDate
                it[nbPlayed] = album.nbPlayed
                it[isInQuickAccess] = album.isInQuickAccess
            }
        }
    }

    override suspend fun deleteAlbum(album: Album) {
        dbQuery {
            AlbumTable.deleteWhere { albumId eq album.albumId.toString() }
        }
    }

    override suspend fun getAllAlbumsFromArtist(artistId: UUID): List<Album> = dbQuery {
        AlbumTable.join(
            otherTable = AlbumArtistTable,
            joinType = JoinType.INNER,
            onColumn = AlbumTable.albumId,
            otherColumn = AlbumArtistTable.albumId,
            additionalConstraint = { AlbumArtistTable.artistId eq artistId.toString() }
        ).selectAll().map(ExposedUtils::resultRowToAlbum)
    }

    override suspend fun getAlbumFromId(albumId: UUID): Album? = dbQuery {
        AlbumTable
            .selectAll()
            .where { AlbumTable.albumId eq albumId.toString() }
            .map(ExposedUtils::resultRowToAlbum)
            .singleOrNull()
    }

    override fun getAlbumWithMusicsAsFlow(albumId: UUID): Flow<AlbumWithMusics?> {
        TODO()
    }

    override suspend fun getAlbumWithMusics(albumId: UUID): AlbumWithMusics {
        TODO("Not yet implemented")
    }

    override fun getAllAlbumsSortByNameAscAsFlow(): Flow<List<Album>> = transaction {
        flowOf(AlbumTable.selectAll().orderBy(AlbumTable.albumName to SortOrder.ASC).map(ExposedUtils::resultRowToAlbum))
    }

    override fun getAllAlbumsWithMusicsSortByNameAscAsFlow(): Flow<List<AlbumWithMusics>> {
        TODO("Not yet implemented")
    }

    override fun getAllAlbumsWithMusicsSortByNameDescAsFlow(): Flow<List<AlbumWithMusics>> {
        TODO("Not yet implemented")
    }

    override fun getAllAlbumsWithMusicsSortByAddedDateAscAsFlow(): Flow<List<AlbumWithMusics>> {
        TODO("Not yet implemented")
    }

    override fun getAllAlbumsWithMusicsSortByAddedDateDescAsFlow(): Flow<List<AlbumWithMusics>> {
        TODO("Not yet implemented")
    }

    override fun getAllAlbumsWithMusicsSortByNbPlayedAscAsFlow(): Flow<List<AlbumWithMusics>> {
        TODO("Not yet implemented")
    }

    override fun getAllAlbumsWithMusicsSortByNbPlayedDescAsFlow(): Flow<List<AlbumWithMusics>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllAlbumsWithArtist(): List<AlbumWithArtist> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllAlbumsWithMusics(): List<AlbumWithMusics> {
        TODO("Not yet implemented")
    }

    override fun getAllAlbumWithArtistFromQuickAccessAsFlow(): Flow<List<AlbumWithArtist>> {
        TODO("Not yet implemented")
    }

    override suspend fun getCorrespondingAlbum(albumName: String, artistId: UUID): Album? = dbQuery {
        AlbumTable.join(
            otherTable = AlbumArtistTable,
            joinType = JoinType.INNER,
            onColumn = AlbumTable.albumId,
            otherColumn = AlbumArtistTable.albumId,
            additionalConstraint = { (AlbumTable.albumName eq albumName) and (AlbumArtistTable.artistId eq artistId.toString())  }
        ).selectAll().map(ExposedUtils::resultRowToAlbum).singleOrNull()
    }

    override suspend fun getPossibleDuplicateAlbum(
        albumId: UUID,
        albumName: String,
        artistId: UUID
    ): Album? = dbQuery {
        AlbumTable.join(
            otherTable = AlbumArtistTable,
            joinType = JoinType.INNER,
            onColumn = AlbumTable.albumId,
            otherColumn = AlbumArtistTable.albumId,
            additionalConstraint = {
                (AlbumTable.albumName eq albumName) and
                        (AlbumArtistTable.artistId eq artistId.toString()) and (AlbumTable.albumId notLike albumId.toString()) }
        ).selectAll().map(ExposedUtils::resultRowToAlbum).singleOrNull()
    }

    override suspend fun updateAlbumCover(newCoverId: UUID, albumId: UUID) {
        dbQuery {
            AlbumTable.update({ AlbumTable.albumId eq albumId.toString() }) {
                it[coverId] = newCoverId.toString()
            }
        }
    }

    override suspend fun updateQuickAccessState(newQuickAccessState: Boolean, albumId: UUID) {
        dbQuery {
            AlbumTable.update({ AlbumTable.albumId eq albumId.toString() }) {
                it[isInQuickAccess] = newQuickAccessState
            }
        }
    }

    override suspend fun getNumberOfAlbumsWithCoverId(coverId: UUID): Int = dbQuery{
        AlbumTable.selectAll().where { AlbumTable.coverId eq coverId.toString() }.count().toInt()
    }

    override suspend fun getNbPlayedOfAlbum(albumId: UUID): Int = dbQuery {
        val listResult = AlbumTable.select(AlbumTable.nbPlayed).where { AlbumTable.albumId eq albumId.toString() }.map{ it[AlbumTable.nbPlayed] }
        return@dbQuery if (listResult.isEmpty()) 0 else listResult[0]
    }

    override suspend fun updateNbPlayed(newNbPlayed: Int, albumId: UUID) {
        dbQuery {
            AlbumTable.update({ AlbumTable.albumId eq albumId.toString() }) {
                it[nbPlayed] = newNbPlayed
            }
        }
    }
}