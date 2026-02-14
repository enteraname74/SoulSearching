package com.github.enteraname74.soulsearching.repository.datasource

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.ArtistPreview
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

    suspend fun upsertAll(artists: List<Artist>)

    /**
     * Deletes an Artist.
     */
    suspend fun deleteAll(artist: Artist)

    suspend fun deleteAll(artistsIds: List<UUID>)

    suspend fun getArtistNamesContainingSearch(search: String): List<String>

    suspend fun toggleCoverFolderMode(isActivated: Boolean)

    /**
     * Retrieves an Artist from its id.
     */
    fun getFromId(artistId: UUID) : Flow<Artist?>

    /**
     * Retrieves a flow of all Artist, sorted by name asc.
     */
    fun getAll(): Flow<List<Artist>>

    fun getAllPaged(): Flow<PagingData<ArtistPreview>>

    /**
     * Tries to find an artist from its name.
     */
    suspend fun getFromName(artistName: String): Artist?

    suspend fun getAllFromName(artistsNames: List<String>): List<Artist>

    /**
     * Retrieves a flow of an ArtistWithMusics.
     */
    fun getArtistWithMusics(artistId: UUID): Flow<ArtistWithMusics?>

    /**
     * Retrieves all artists linked to a music.
     */
    fun getArtistsOfMusic(musicId: UUID): Flow<List<Artist>>

    fun getAllFromQuickAccess(): Flow<List<ArtistPreview>>

    suspend fun getDuplicatedArtist(
        artistId: UUID,
        artistName: String
    ): ArtistWithMusics?

    fun getArtistsWistMostMusics(): Flow<List<ArtistPreview>>

    suspend fun cleanAllCovers()

    fun getMostListened(): Flow<List<ArtistPreview>>

    fun getArtistPreview(artistId: UUID): Flow<ArtistPreview?>

    fun searchAll(search: String): Flow<List<ArtistPreview>>
}