package com.github.enteraname74.localdesktop.daoimpl

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.localdesktop.dao.ArtistDao
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Implementation of the ArtistDao for Exposed.
 */
class ExposedArtistDaoImpl: ArtistDao {
    override suspend fun insertArtist(artist: Artist) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteArtist(artist: Artist) {
        TODO("Not yet implemented")
    }

    override suspend fun getArtistFromId(artistId: UUID): Artist? {
        TODO("Not yet implemented")
    }

    override fun getAllArtistsSortByNameAscAsFlow(): Flow<List<Artist>> {
        TODO("Not yet implemented")
    }

    override fun getAllArtistsWithMusicsSortByNameAscAsFlow(): Flow<List<ArtistWithMusics>> {
        TODO("Not yet implemented")
    }

    override fun getAllArtistWithMusicsSortByNameDescAsFlow(): Flow<List<ArtistWithMusics>> {
        TODO("Not yet implemented")
    }

    override fun getAllArtistWithMusicsSortByAddedDateAscAsFlow(): Flow<List<ArtistWithMusics>> {
        TODO("Not yet implemented")
    }

    override fun getAllArtistWithMusicsSortByAddedDateDescAsFlow(): Flow<List<ArtistWithMusics>> {
        TODO("Not yet implemented")
    }

    override fun getAllArtistWithMusicsSortByNbPlayedAscAsFlow(): Flow<List<ArtistWithMusics>> {
        TODO("Not yet implemented")
    }

    override fun getAllArtistWithMusicsSortByNbPlayedDescAsFlow(): Flow<List<ArtistWithMusics>> {
        TODO("Not yet implemented")
    }

    override suspend fun getPossibleDuplicatedArtist(
        artistId: UUID,
        artistName: String
    ): ArtistWithMusics? {
        TODO("Not yet implemented")
    }

    override suspend fun getArtistFromInfo(artistName: String): Artist? {
        TODO("Not yet implemented")
    }

    override fun getArtistWithMusicsAsFlow(artistId: UUID): Flow<ArtistWithMusics?> {
        TODO("Not yet implemented")
    }

    override fun getAllArtistWithMusicsFromQuickAccessAsFlow(): Flow<List<ArtistWithMusics>> {
        TODO("Not yet implemented")
    }

    override suspend fun getArtistWithMusics(artistId: UUID): ArtistWithMusics? {
        TODO("Not yet implemented")
    }

    override suspend fun updateArtistCover(newCoverId: UUID, artistId: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun getNumberOfArtistsWithCoverId(coverId: UUID): Int {
        TODO("Not yet implemented")
    }

    override suspend fun updateQuickAccessState(newQuickAccessState: Boolean, artistId: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun getNbPlayedOfArtist(artistId: UUID): Int {
        TODO("Not yet implemented")
    }

    override suspend fun updateNbPlayed(newNbPlayed: Int, artistId: UUID) {
        TODO("Not yet implemented")
    }
}