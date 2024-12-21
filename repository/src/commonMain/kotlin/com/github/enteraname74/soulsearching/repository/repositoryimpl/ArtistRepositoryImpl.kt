package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.soulsearching.repository.datasource.ArtistDataSource
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * Repository of an Artist.
 */
class ArtistRepositoryImpl(
    private val artistDataSource: ArtistDataSource,
): ArtistRepository {

    /**
     * Inserts or updates an artist.
     */
    override suspend fun upsert(artist: Artist) = artistDataSource.upsert(
        artist = artist
    )

    override suspend fun upsertAll(artists: List<Artist>) {
        artistDataSource.upsertAll(artists)
    }

    /**
     * Deletes an Artist.
     */
    override suspend fun delete(artist: Artist) = artistDataSource.deleteAll(
        artist = artist
    )

    override suspend fun deleteAll(artistsIds: List<UUID>) {
        artistDataSource.deleteAll(artistsIds)
    }

    /**
     * Retrieves an Artist from its id.
     */
    override fun getFromId(artistId: UUID): Flow<Artist?> = artistDataSource.getFromId(
        artistId = artistId
    )

    override suspend fun getFromName(artistName: String): Artist? =
        artistDataSource.getFromName(artistName = artistName)

    /**
     * Retrieves a flow of all Artist, sorted by name asc.
     */
    override fun getAll(): Flow<List<Artist>> =
        artistDataSource.getAll()

    /**
     * Retrieves a flow of all ArtistWithMusics, sorted by name asc.
     */
    override fun getAllArtistWithMusics(): Flow<List<ArtistWithMusics>> =
        artistDataSource.getAllArtistWithMusics()

    /**
     * Retrieves a flow of an ArtistWithMusics.
     */
    override fun getArtistWithMusics(artistId: UUID): Flow<ArtistWithMusics?> =
        artistDataSource.getArtistWithMusics(
            artistId = artistId
        )

    override fun getArtistsOfMusic(musicId: UUID): Flow<List<Artist>> =
        artistDataSource.getArtistsOfMusic(musicId)

}