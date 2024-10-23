package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.ArtistWithMusics
import kotlinx.coroutines.flow.Flow
import java.util.*

interface ArtistRepository {

    /**
     * Inserts or updates an artist.
     */
    suspend fun upsert(artist: Artist)

    suspend fun upsertAll(artists: List<Artist>)

    /**
     * Deletes an Artist.
     */
    suspend fun delete(artist: Artist)

    suspend fun deleteAll(artistsIds: List<UUID>)

    /**
     * Retrieves an Artist from its id.
     */
    fun getFromId(artistId: UUID): Flow<Artist?>

    /**
     * Retrieves an Artist from its name.
     */
    suspend fun getFromName(artistName: String): Artist?

    /**
     * Retrieves a flow of all Artist.
     */
    fun getAll(): Flow<List<Artist>>

    /**
     * Retrieves a flow of all ArtistWithMusics.
     */
    fun getAllArtistWithMusics(): Flow<List<ArtistWithMusics>>

    /**
     * Retrieves a flow of an ArtistWithMusics.
     */
    fun getArtistWithMusics(artistId: UUID): Flow<ArtistWithMusics?>
}