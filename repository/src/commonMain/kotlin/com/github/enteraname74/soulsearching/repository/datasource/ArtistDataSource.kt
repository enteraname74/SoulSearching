package com.github.enteraname74.soulsearching.repository.datasource

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.ArtistPreview
import com.github.enteraname74.domain.model.ArtistWithMusics
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

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
    suspend fun delete(artist: Artist)

    suspend fun deleteAll(artistsIds: List<Uuid>)

    suspend fun deleteAllEmpty()

    suspend fun getArtistNamesContainingSearch(search: String): List<String>

    suspend fun toggleCoverFolderMode(isActivated: Boolean)

    /**
     * Retrieves an Artist from its id.
     */
    fun getFromId(artistId: Uuid) : Flow<Artist?>

    suspend fun getFromRemoteId(remoteId: Uuid): Artist?

    fun getFromIds(artistIds: List<Uuid>) : Flow<List<ArtistWithMusics>>

    fun getAllPaged(): Flow<PagingData<ArtistPreview>>

    suspend fun getAll(page: Int, pageSize: Int): List<ArtistPreview>

    /**
     * Tries to find an artist from its name.
     */
    suspend fun getFromName(artistName: String): Artist?

    suspend fun getAllFromName(artistsNames: List<String>): List<Artist>

    /**
     * Retrieves a flow of an ArtistWithMusics.
     */
    fun getArtistWithMusics(artistId: Uuid): Flow<ArtistWithMusics?>

    /**
     * Retrieves all artists linked to a music.
     */
    fun getArtistsOfMusic(musicId: Uuid): Flow<List<Artist>>

    fun getAllFromQuickAccess(): Flow<List<ArtistPreview>>

    suspend fun getDuplicatedArtist(
        artistId: Uuid,
        artistName: String
    ): ArtistWithMusics?

    fun getArtistsWithMostMusics(): Flow<List<ArtistPreview>>

    suspend fun cleanAllCovers()

    fun getMostListened(): Flow<List<ArtistPreview>>

    fun getArtistPreview(artistId: Uuid): Flow<ArtistPreview?>

    fun searchAll(search: String): Flow<List<ArtistPreview>>

    suspend fun getPotentialMultipleArtists(): List<Artist>
}
