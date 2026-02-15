package com.github.enteraname74.domain.repository

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.ArtistPreview
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

    suspend fun toggleCoverFolderMode(isActivated: Boolean)

    fun getAllPaged(): Flow<PagingData<ArtistPreview>>

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

    suspend fun getPotentialMultipleArtists(): List<Artist>
}