package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.util.FlowResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

interface ArtistRepository {
    /**
     * Inserts or updates an artist.
     */
    suspend fun upsert(artist: Artist): SoulResult<Unit>
    suspend fun upsertAll(artists: List<Artist>)

    /**
     * Deletes an Artist.
     */
    suspend fun delete(artist: Artist): SoulResult<Unit>
    suspend fun deleteAll(artistsIds: List<UUID>): SoulResult<Unit>
    suspend fun deleteAll(dataMode: DataMode)

    suspend fun getArtistNamesContainingSearch(search: String): List<String>

    /**
     * Retrieves an Artist from its id.
     */
    fun getFromId(artistId: UUID): Flow<Artist?>

    /**
     * Retrieves an Artist from its name.
     */
    suspend fun getFromName(artistName: String): Artist?

    suspend fun getAllFromName(artistsNames: List<String>): List<Artist>

    /**
     * Retrieves a flow of all Artist.
     */
    fun getAll(dataMode: DataMode? = null): Flow<List<Artist>>

    /**
     * Retrieves a flow of all ArtistWithMusics.
     */
    fun getAllArtistWithMusics(dataMode: DataMode? = null): Flow<List<ArtistWithMusics>>

    /**
     * Retrieves a flow of an ArtistWithMusics.
     */
    fun getArtistWithMusics(artistId: UUID): Flow<ArtistWithMusics?>

    /**
     * Retrieves all artists linked to a music.
     */
    fun getArtistsOfMusic(musicId: UUID): Flow<List<Artist>>

    /**
     * Synchronize remote artists of the users with the cloud
     */
    suspend fun syncWithCloud(): SoulResult<Unit>
}