package com.github.enteraname74.localdesktop.datasourceimpl

import com.github.enteraname74.domain.datasource.ArtistDataSource
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.localdesktop.dao.ArtistDao
import java.util.UUID

/**
 * Implementation of the ArtistDataSource with Exposed.
 */
internal class ExposedArtistDataSourceImpl(
    private val artistDao: ArtistDao
) : ArtistDataSource {
    override suspend fun insertArtist(artist: Artist) = artistDao.insertArtist(artist = artist)

    override suspend fun deleteArtist(artist: Artist) = artistDao.deleteArtist(artist = artist)

    override suspend fun getArtistFromId(artistId: UUID) =
        artistDao.getArtistFromId(artistId = artistId)

    override fun getAllArtistsSortByNameAscAsFlow() = artistDao.getAllArtistsSortByNameAscAsFlow()

    override fun getAllArtistsWithMusicsSortByNameAscAsFlow() =
        artistDao.getAllArtistsWithMusicsSortByNameAscAsFlow()

    override fun getAllArtistWithMusicsSortByNameDescAsFlow() =
        artistDao.getAllArtistWithMusicsSortByNameDescAsFlow()

    override fun getAllArtistWithMusicsSortByAddedDateAscAsFlow() =
        artistDao.getAllArtistWithMusicsSortByAddedDateAscAsFlow()

    override fun getAllArtistWithMusicsSortByAddedDateDescAsFlow() =
        artistDao.getAllArtistWithMusicsSortByAddedDateDescAsFlow()

    override fun getAllArtistWithMusicsSortByNbPlayedAscAsFlow() =
        artistDao.getAllArtistWithMusicsSortByNbPlayedAscAsFlow()

    override fun getAllArtistWithMusicsSortByNbPlayedDescAsFlow() =
        artistDao.getAllArtistWithMusicsSortByNbPlayedDescAsFlow()

    override suspend fun getPossibleDuplicatedArtist(
        artistId: UUID,
        artistName: String
    ) = artistDao.getPossibleDuplicatedArtist(
        artistId = artistId,
        artistName = artistName
    )

    override suspend fun getArtistFromInfo(artistName: String) =
        artistDao.getArtistFromInfo(artistName = artistName)

    override fun getArtistWithMusicsAsFlow(artistId: UUID) =
        artistDao.getArtistWithMusicsAsFlow(artistId = artistId)

    override fun getAllArtistWithMusicsFromQuickAccessAsFlow() =
        artistDao.getAllArtistWithMusicsFromQuickAccessAsFlow()

    override suspend fun getArtistWithMusics(artistId: UUID) =
        artistDao.getArtistWithMusics(artistId = artistId)

    override suspend fun updateArtistCover(newCoverId: UUID, artistId: UUID) =
        artistDao.updateArtistCover(
            newCoverId = newCoverId,
            artistId = artistId
        )

    override suspend fun getNumberOfArtistsWithCoverId(coverId: UUID) =
        artistDao.getNumberOfArtistsWithCoverId(
            coverId = coverId
        )

    override suspend fun updateQuickAccessState(newQuickAccessState: Boolean, artistId: UUID) =
        artistDao.updateQuickAccessState(
            newQuickAccessState = newQuickAccessState,
            artistId = artistId
        )

    override suspend fun getNbPlayedOfArtist(artistId: UUID) =
        artistDao.getNbPlayedOfArtist(artistId = artistId)

    override suspend fun updateNbPlayed(newNbPlayed: Int, artistId: UUID) =
        artistDao.updateNbPlayed(
            newNbPlayed = newNbPlayed,
            artistId = artistId
        )
}