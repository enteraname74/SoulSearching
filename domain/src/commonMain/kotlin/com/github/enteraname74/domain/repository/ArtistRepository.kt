package com.github.enteraname74.domain.repository

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.ArtistPreview
import com.github.enteraname74.domain.model.ArtistWithMusics
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface ArtistRepository {

    /**
     * Inserts or updates an artist.
     */
    suspend fun upsert(artist: Artist)

    suspend fun upsertAll(artists: List<Artist>)

    suspend fun delete(artist: Artist)

    suspend fun deleteAll(artistsIds: List<Uuid>)

    suspend fun deleteAllEmpty()

    suspend fun getArtistNamesContainingSearch(search: String): List<String>

    /**
     * Retrieves an Artist from its id.
     */
    fun getFromId(artistId: Uuid): Flow<Artist?>

    suspend fun getFromRemoteId(remoteId: Uuid): Artist?

    fun getFromIds(artistIds: List<Uuid>) : Flow<List<ArtistWithMusics>>

    /**
     * Retrieves an Artist from its name.
     */
    suspend fun getFromName(artistName: String): Artist?

    suspend fun getAllFromName(artistsNames: List<String>): List<Artist>

    suspend fun toggleCoverFolderMode(isActivated: Boolean)

    fun getAllPaged(): Flow<PagingData<ArtistPreview>>

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

    fun getArtistsWistMostMusics(): Flow<List<ArtistPreview>>

    suspend fun cleanAllCovers()

    fun getMostListened(): Flow<List<ArtistPreview>>

    fun getArtistPreview(artistId: Uuid): Flow<ArtistPreview?>

    fun searchAll(search: String): Flow<List<ArtistPreview>>

    suspend fun getPotentialMultipleArtists(): List<Artist>
}
