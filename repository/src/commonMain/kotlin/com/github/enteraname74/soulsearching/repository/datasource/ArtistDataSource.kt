package com.github.enteraname74.soulsearching.repository.datasource

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
    suspend fun upsert(artist: Artist)

    /**
     * Deletes an Artist.
     */
    suspend fun delete(artist: Artist)

    /**
     * Retrieves an Artist from its id.
     */
    fun getFromId(artistId: UUID) : Flow<Artist?>

    /**
     * Retrieves a flow of all Artist, sorted by name asc.
     */
    fun getAll(): Flow<List<Artist>>

    /**
     * Retrieves a flow of all ArtistWithMusics, sorted by name asc.
     */
    fun getAllArtistWithMusics(): Flow<List<ArtistWithMusics>>

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
    fun getArtistWithMusics(artistId: UUID): Flow<ArtistWithMusics?>

    /**
     * Update the cover of an Artist.
     */
    suspend fun updateArtistCover(newCoverId : UUID, artistId : UUID)
}