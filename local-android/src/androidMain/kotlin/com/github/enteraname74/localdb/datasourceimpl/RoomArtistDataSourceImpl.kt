package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.domain.datasource.ArtistDataSource
import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.model.toArtist
import com.github.enteraname74.localdb.model.toArtistWithMusics
import com.github.enteraname74.localdb.model.toRoomArtist
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.ArtistWithMusics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

/**
 * Implementation of the ArtistDataSource with Room's DAO.
 */
internal class RoomArtistDataSourceImpl(
    private val appDatabase: AppDatabase
) : ArtistDataSource {
    override suspend fun insertArtist(artist: Artist) {
        appDatabase.artistDao.insertArtist(
            roomArtist = artist.toRoomArtist()
        )
    }

    override suspend fun deleteArtist(artist: Artist) {
        appDatabase.artistDao.deleteArtist(
            roomArtist = artist.toRoomArtist()
        )
    }

    override suspend fun getArtistFromId(artistId: UUID): Artist? {
        return appDatabase.artistDao.getArtistFromId(
            artistId = artistId
        )?.toArtist()
    }

    override fun getAllArtistsSortByNameAscAsFlow(): Flow<List<Artist>> {
        return appDatabase.artistDao.getAllArtistsSortByNameAsFlow().map { list ->
            list.map { it.toArtist() }
        }
    }

    override fun getAllArtistsWithMusicsSortByNameAscAsFlow(): Flow<List<ArtistWithMusics>> {
        return appDatabase.artistDao.getAllArtistsWithMusicsSortByNameAscAsFlow().map { list ->
            list.filterNotNull().map { it.toArtistWithMusics() }
        }
    }

    override fun getAllArtistWithMusicsSortByNameDescAsFlow(): Flow<List<ArtistWithMusics>> {
        return appDatabase.artistDao.getAllArtistWithMusicsSortByNameDescAsFlow().map { list ->
            list.filterNotNull().map { it.toArtistWithMusics() }
        }
    }

    override fun getAllArtistWithMusicsSortByAddedDateAscAsFlow(): Flow<List<ArtistWithMusics>> {
        return appDatabase.artistDao.getAllArtistWithMusicsSortByAddedDateAscAsFlow().map { list ->
            list.filterNotNull().map { it.toArtistWithMusics() }
        }
    }

    override fun getAllArtistWithMusicsSortByAddedDateDescAsFlow(): Flow<List<ArtistWithMusics>> {
        return appDatabase.artistDao.getAllArtistWithMusicsSortByAddedDateDescAsFlow().map { list ->
            list.filterNotNull().map { it.toArtistWithMusics() }
        }
    }

    override fun getAllArtistWithMusicsSortByNbPlayedAscAsFlow(): Flow<List<ArtistWithMusics>> {
        return appDatabase.artistDao.getAllArtistWithMusicsSortByNbPlayedAscAsFlow().map { list ->
            list.filterNotNull().map { it.toArtistWithMusics() }
        }
    }

    override fun getAllArtistWithMusicsSortByNbPlayedDescAsFlow(): Flow<List<ArtistWithMusics>> {
        return appDatabase.artistDao.getAllArtistWithMusicsSortByNbPlayedDescAsFlow().map { list ->
            list.filterNotNull().map { it.toArtistWithMusics() }
        }
    }

    override suspend fun getPossibleDuplicatedArtist(
        artistId: UUID,
        artistName: String
    ): ArtistWithMusics? {
        return appDatabase.artistDao.getPossibleDuplicatedArtistName(
            artistId = artistId,
            artistName = artistName
        )?.toArtistWithMusics()
    }

    override suspend fun getArtistFromInfo(artistName: String): Artist? {
        return appDatabase.artistDao.getArtistFromInfo(
            artistName = artistName
        )?.toArtist()
    }

    override fun getArtistWithMusicsAsFlow(artistId: UUID): Flow<ArtistWithMusics?> {
        return appDatabase.artistDao.getArtistWithMusicsAsFlow(
            artistId = artistId
        ).map { it?.toArtistWithMusics() }
    }

    override fun getAllArtistWithMusicsFromQuickAccessAsFlow(): Flow<List<ArtistWithMusics>> {
        return appDatabase.artistDao.getAllArtistsFromQuickAccessAsFlow().map { list ->
            list.filterNotNull().map { it.toArtistWithMusics() }
        }
    }

    override suspend fun getArtistWithMusics(artistId: UUID): ArtistWithMusics? {
        return appDatabase.artistDao.getArtistWithMusics(
            artistId = artistId
        )?.toArtistWithMusics()
    }

    override suspend fun updateArtistCover(newCoverId: UUID, artistId: UUID) {
        appDatabase.artistDao.updateArtistCover(
            newCoverId = newCoverId,
            artistId = artistId
        )
    }

    override suspend fun getNumberOfArtistsWithCoverId(coverId: UUID): Int {
        return appDatabase.artistDao.getNumberOfArtistsWithCoverId(
            coverId = coverId
        )
    }

    override suspend fun updateQuickAccessState(newQuickAccessState: Boolean, artistId: UUID) {
        appDatabase.artistDao.updateQuickAccessState(
            newQuickAccessState = newQuickAccessState,
            artistId = artistId
        )
    }

    override suspend fun getNbPlayedOfArtist(artistId: UUID): Int {
        return appDatabase.artistDao.getNbPlayedOfArtist(
            artistId = artistId
        ) ?: 0
    }

    override suspend fun updateNbPlayed(newNbPlayed: Int, artistId: UUID) {
        appDatabase.artistDao.updateNbPlayed(
            newNbPlayed = newNbPlayed,
            artistId = artistId
        )
    }
}