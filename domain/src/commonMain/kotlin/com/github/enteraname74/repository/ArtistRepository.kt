package com.github.enteraname74.repository

import com.github.enteraname74.datasource.ArtistDataSource
import com.github.enteraname74.model.Artist
import com.github.enteraname74.model.ArtistWithMusics
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Repository of a Artist.
 */
class ArtistRepository(
    private val artistDataSource: ArtistDataSource
) {
    /**
     * Inserts or updates an artist.
     */
    suspend fun insertArtist(artist: Artist) = artistDataSource.insertArtist(
        artist = artist
    )

    /**
     * Deletes an Artist.
     */
    suspend fun deleteArtist(artist: Artist) = artistDataSource.deleteArtist(
        artist = artist
    )

    /**
     * Retrieves an Artist from its id.
     */
    suspend fun getArtistFromId(artistId: UUID): Artist? = artistDataSource.getArtistFromId(
        artistId = artistId
    )

    /**
     * Retrieves a flow of all Artist, sorted by name asc.
     */
    fun getAllArtistsSortByNameAscAsFlow(): Flow<List<Artist>> =
        artistDataSource.getAllArtistsSortByNameAscAsFlow()

    /**
     * Retrieves a flow of all ArtistWithMusics, sorted by name asc.
     */
    fun getAllArtistsWithMusicsSortByNameAscAsFlow(): Flow<List<ArtistWithMusics>> =
        artistDataSource.getAllArtistsWithMusicsSortByNameAscAsFlow()

    /**
     * Retrieves a flow of all ArtistWithMusics, sorted by name desc.
     */
    fun getAllArtistWithMusicsSortByNameDescAsFlow(): Flow<List<ArtistWithMusics>> =
        artistDataSource.getAllArtistWithMusicsSortByNameDescAsFlow()

    /**
     * Retrieves a flow of all ArtistWithMusics, sorted by added date asc.
     */
    fun getAllArtistWithMusicsSortByAddedDateAscAsFlow(): Flow<List<ArtistWithMusics>> =
        artistDataSource.getAllArtistWithMusicsSortByAddedDateAscAsFlow()

    /**
     * Retrieves a flow of all ArtistWithMusics, sorted by added date desc.
     */
    fun getAllArtistWithMusicsSortByAddedDateDescAsFlow(): Flow<List<ArtistWithMusics>> =
        artistDataSource.getAllArtistWithMusicsSortByAddedDateDescAsFlow()

    /**
     * Retrieves a flow of all ArtistWithMusics, sorted by nb played asc.
     */
    fun getAllArtistWithMusicsSortByNbPlayedAscAsFlow(): Flow<List<ArtistWithMusics>> =
        artistDataSource.getAllArtistWithMusicsSortByNbPlayedAscAsFlow()

    /**
     * Retrieves a flow of all ArtistWithMusics, sorted by nb played desc.
     */
    fun getAllArtistWithMusicsSortByNbPlayedDescAsFlow(): Flow<List<ArtistWithMusics>> =
        artistDataSource.getAllArtistWithMusicsSortByNbPlayedDescAsFlow()

    /**
     * Tries to find a duplicate artist.
     */
    suspend fun getPossibleDuplicatedArtist(artistId: UUID, artistName: String): ArtistWithMusics? =
        artistDataSource.getPossibleDuplicatedArtist(
            artistId = artistId,
            artistName = artistName
        )

    /**
     * Tries to find an artist from its name.
     */
    suspend fun getArtistFromInfo(artistName: String): Artist? = artistDataSource.getArtistFromInfo(
        artistName = artistName
    )

    /**
     * Retrieves a flow of an ArtistWithMusics.
     */
    fun getArtistWithMusicsAsFlow(artistId: UUID): Flow<ArtistWithMusics?> =
        artistDataSource.getArtistWithMusicsAsFlow(
            artistId = artistId
        )

    /**
     * Retrieves all ArtistWithMusics from the quick access
     */
    fun getAllArtistWithMusicsFromQuickAccessAsFlow(): Flow<List<ArtistWithMusics>> =
        artistDataSource.getAllArtistWithMusicsFromQuickAccessAsFlow()

    /**
     * Retrieves an ArtistWithMusics.
     */
    suspend fun getArtistWithMusics(artistId: UUID): ArtistWithMusics? =
        artistDataSource.getArtistWithMusics(
            artistId = artistId
        )

    /**
     * Update the cover of an Artist.
     */
    suspend fun updateArtistCover(newCoverId: UUID, artistId: UUID) =
        artistDataSource.updateArtistCover(
            newCoverId = newCoverId,
            artistId = artistId
        )

    /**
     * Get the number of artists sharing the same cover.
     */
    suspend fun getNumberOfArtistsWithCoverId(coverId: UUID): Int =
        artistDataSource.getNumberOfArtistsWithCoverId(
            coverId = coverId
        )

    /**
     * Update the quick access status of an Artist.
     */
    suspend fun updateQuickAccessState(newQuickAccessState: Boolean, artistId: UUID) =
        artistDataSource.updateQuickAccessState(
            newQuickAccessState = newQuickAccessState,
            artistId = artistId
        )

    /**
     * Get the number of time an Artist has been played.
     */
    suspend fun getNbPlayedOfArtist(artistId: UUID): Int = artistDataSource.getNbPlayedOfArtist(
        artistId = artistId
    )

    /**
     * Update the total of played time of an Artist.
     */
    suspend fun updateNbPlayed(newNbPlayed: Int, artistId: UUID) = artistDataSource.updateNbPlayed(
        newNbPlayed = newNbPlayed,
        artistId = artistId
    )
}