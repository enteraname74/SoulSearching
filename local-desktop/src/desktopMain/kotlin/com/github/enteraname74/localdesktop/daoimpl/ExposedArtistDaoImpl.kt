package com.github.enteraname74.localdesktop.daoimpl

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.localdesktop.dao.ArtistDao
import com.github.enteraname74.localdesktop.dbQuery
import com.github.enteraname74.localdesktop.tables.ArtistTable
import com.github.enteraname74.localdesktop.utils.ExposedUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.upsert
import java.util.UUID

/**
 * Implementation of the ArtistDao for Exposed.
 */
class ExposedArtistDaoImpl: ArtistDao {
    override suspend fun insertArtist(artist: Artist) {
        dbQuery { 
            ArtistTable.upsert {
                it[artistId] = artist.artistId.toString()
                it[artistName] = artist.artistName
                it[coverId] = artist.coverId?.toString()
                it[addedDate] = artist.addedDate
                it[nbPlayed] = artist.nbPlayed
                it[isInQuickAccess] = artist.isInQuickAccess
            }
        }
    }

    override suspend fun deleteArtist(artist: Artist) {
        dbQuery { 
            ArtistTable.deleteWhere { artistId eq artist.artistId.toString() }
        }
    }

    override suspend fun getArtistFromId(artistId: UUID): Artist? = dbQuery { 
        ArtistTable
            .selectAll()
            .where { ArtistTable.artistId eq artistId.toString() }
            .map(ExposedUtils::resultRowToArtist)
            .singleOrNull()
    }

    override fun getAllArtistsSortByNameAscAsFlow(): Flow<List<Artist>> = transaction { 
        flowOf(
            ArtistTable
                .selectAll()
                .map(ExposedUtils::resultRowToArtist)
        )
    }

    override fun getAllArtistsWithMusicsSortByNameAscAsFlow(): Flow<List<ArtistWithMusics>> {
        return flowOf(emptyList())
    }

    override fun getAllArtistWithMusicsSortByNameDescAsFlow(): Flow<List<ArtistWithMusics>> {
        return flowOf(emptyList())
    }

    override fun getAllArtistWithMusicsSortByAddedDateAscAsFlow(): Flow<List<ArtistWithMusics>> {
        return flowOf(emptyList())
    }

    override fun getAllArtistWithMusicsSortByAddedDateDescAsFlow(): Flow<List<ArtistWithMusics>> {
        return flowOf(emptyList())
    }

    override fun getAllArtistWithMusicsSortByNbPlayedAscAsFlow(): Flow<List<ArtistWithMusics>> {
        return flowOf(emptyList())
    }

    override fun getAllArtistWithMusicsSortByNbPlayedDescAsFlow(): Flow<List<ArtistWithMusics>> {
        return flowOf(emptyList())
    }

    override suspend fun getPossibleDuplicatedArtist(
        artistId: UUID,
        artistName: String
    ): ArtistWithMusics? {
        return null
    }

    override suspend fun getArtistFromInfo(artistName: String): Artist? = dbQuery { 
        ArtistTable
            .selectAll()
            .where { ArtistTable.artistName eq artistName }
            .map(ExposedUtils::resultRowToArtist)
            .singleOrNull()
    }

    override fun getArtistWithMusicsAsFlow(artistId: UUID): Flow<ArtistWithMusics?> {
        return flowOf(null)
    }

    override fun getAllArtistWithMusicsFromQuickAccessAsFlow(): Flow<List<ArtistWithMusics>> {
        return flowOf(emptyList())
    }

    override suspend fun getArtistWithMusics(artistId: UUID): ArtistWithMusics? = dbQuery {
        null
    }

    override suspend fun updateArtistCover(newCoverId: UUID, artistId: UUID) {
        dbQuery {
            ArtistTable.update({ArtistTable.artistId eq artistId.toString()}) {
                it[coverId] = newCoverId.toString()
            }
        }
    }

    override suspend fun getNumberOfArtistsWithCoverId(coverId: UUID): Int = dbQuery {
        ArtistTable
            .selectAll()
            .where { ArtistTable.coverId eq coverId.toString() }
            .count()
            .toInt()
    }

    override suspend fun updateQuickAccessState(newQuickAccessState: Boolean, artistId: UUID) {
        dbQuery {
            ArtistTable.update({ArtistTable.artistId eq artistId.toString()}) {
                it[isInQuickAccess] = newQuickAccessState
            }
        }
    }

    override suspend fun getNbPlayedOfArtist(artistId: UUID): Int = dbQuery {
        val artists = ArtistTable
            .select(ArtistTable.nbPlayed)
            .where { ArtistTable.artistId eq artistId.toString() }
            .map{ it[ArtistTable.nbPlayed] }

        if (artists.isEmpty()) 0 else artists[0]
    }

    override suspend fun updateNbPlayed(newNbPlayed: Int, artistId: UUID) {
        dbQuery {
            ArtistTable.update({ArtistTable.artistId eq artistId.toString()}) {
                it[nbPlayed] = newNbPlayed
            }
        }
    }
}