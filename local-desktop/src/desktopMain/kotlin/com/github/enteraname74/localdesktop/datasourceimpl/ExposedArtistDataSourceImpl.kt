package com.github.enteraname74.localdesktop.datasourceimpl

import com.github.enteraname74.domain.datasource.ArtistDataSource
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.ArtistWithMusics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

class ExposedArtistDataSourceImpl : ArtistDataSource {
    override suspend fun insertArtist(artist: Artist) {

    }

    override suspend fun deleteArtist(artist: Artist) {

    }

    override suspend fun getArtistFromId(artistId: UUID): Artist? {
        return null
    }

    override fun getAllArtistsSortByNameAscAsFlow(): Flow<List<Artist>> {
        return flowOf(emptyList())
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

    override suspend fun getArtistFromInfo(artistName: String): Artist? {
        return null
    }

    override fun getArtistWithMusicsAsFlow(artistId: UUID): Flow<ArtistWithMusics?> {
        return flowOf(null)
    }

    override fun getAllArtistWithMusicsFromQuickAccessAsFlow(): Flow<List<ArtistWithMusics>> {
        return flowOf(emptyList())
    }

    override suspend fun getArtistWithMusics(artistId: UUID): ArtistWithMusics? {
        return null
    }

    override suspend fun updateArtistCover(newCoverId: UUID, artistId: UUID) {

    }

    override suspend fun getNumberOfArtistsWithCoverId(coverId: UUID): Int {
        return 0
    }

    override suspend fun updateQuickAccessState(newQuickAccessState: Boolean, artistId: UUID) {

    }

    override suspend fun getNbPlayedOfArtist(artistId: UUID): Int {
        return 0
    }

    override suspend fun updateNbPlayed(newNbPlayed: Int, artistId: UUID) {

    }
}