package com.github.enteraname74.domain.datasource

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.ArtistWithMusics
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Data source of an Artist.
 */
interface ArtistDataSource {
    /**
     * Inserts or updates an artist.
     */
    suspend fun insertArtist(artist: Artist)

    /**
     * Deletes an Artist.
     */
    suspend fun deleteArtist(artist: Artist)

    /**
     * Retrieves an Artist from its id.
     */
    suspend fun getArtistFromId(artistId: UUID) : Artist?

    /**
     * Retrieves a flow of all Artist, sorted by name asc.
     */
    fun getAllArtistsSortByNameAscAsFlow(): Flow<List<Artist>>

    /**
     * Retrieves a flow of all ArtistWithMusics, sorted by name asc.
     */
    fun getAllArtistsWithMusicsSortByNameAscAsFlow(): Flow<List<ArtistWithMusics>>

    /**
     * Retrieves a flow of all ArtistWithMusics, sorted by name desc.
     */
    fun getAllArtistWithMusicsSortByNameDescAsFlow(): Flow<List<ArtistWithMusics>>

    /**
     * Retrieves a flow of all ArtistWithMusics, sorted by added date asc.
     */
    fun getAllArtistWithMusicsSortByAddedDateAscAsFlow(): Flow<List<ArtistWithMusics>>

    /**
     * Retrieves a flow of all ArtistWithMusics, sorted by added date desc.
     */
    fun getAllArtistWithMusicsSortByAddedDateDescAsFlow(): Flow<List<ArtistWithMusics>>

    /**
     * Retrieves a flow of all ArtistWithMusics, sorted by nb played asc.
     */
    fun getAllArtistWithMusicsSortByNbPlayedAscAsFlow(): Flow<List<ArtistWithMusics>>

    /**
     * Retrieves a flow of all ArtistWithMusics, sorted by nb played desc.
     */
    fun getAllArtistWithMusicsSortByNbPlayedDescAsFlow(): Flow<List<ArtistWithMusics>>

    /**
     * Tries to find a duplicate artist.
     */
    suspend fun getPossibleDuplicatedArtist(artistId: UUID, artistName: String) : ArtistWithMusics?

    /**
     * Tries to find an artist from its name.
     */
    suspend fun getArtistFromInfo(artistName: String): Artist?

    /**
     * Retrieves a flow of an ArtistWithMusics.
     */
    fun getArtistWithMusicsAsFlow(artistId: UUID): Flow<ArtistWithMusics>

    /**
     * Retrieves all ArtistWithMusics from the quick access
     */
    fun getAllArtistWithMusicsFromQuickAccessAsFlow(): Flow<List<ArtistWithMusics>>

    /**
     * Retrieves an ArtistWithMusics.
     */
    suspend fun getArtistWithMusics(artistId: UUID): ArtistWithMusics

    /**
     * Update the cover of an Artist.
     */
    suspend fun updateArtistCover(newCoverId : UUID, artistId : UUID)

    /**
     * Get the number of artists sharing the same cover.
     */
    fun getNumberOfArtistsWithCoverId(coverId : UUID) : Int

    /**
     * Update the quick access status of an Artist.
     */
    fun updateQuickAccessState(newQuickAccessState: Boolean, artistId: UUID)

    /**
     * Get the number of time an Artist has been played.
     */
    fun getNbPlayedOfArtist(artistId: UUID): Int

    /**
     * Update the total of played time of an Artist.
     */
    fun updateNbPlayed(newNbPlayed: Int, artistId: UUID)
}