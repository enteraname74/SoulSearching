package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.soulsearching.repository.datasource.artist.ArtistLocalDataSource
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * Repository of an Artist.
 */
class ArtistRepositoryImpl(
    private val artistLocalDataSource: ArtistLocalDataSource,
): ArtistRepository {

    /**
     * Inserts or updates an artist.
     */
    override suspend fun upsert(artist: Artist) = artistLocalDataSource.upsert(
        artist = artist
    )

    override suspend fun upsertAll(artists: List<Artist>) {
        artistLocalDataSource.upsertAll(artists)
    }

    /**
     * Deletes an Artist.
     */
    override suspend fun delete(artist: Artist) = artistLocalDataSource.deleteAll(
        artist = artist
    )

    override suspend fun deleteAll(artistsIds: List<UUID>) {
        artistLocalDataSource.deleteAll(artistsIds)
    }

    /**
     * Retrieves an Artist from its id.
     */
    override fun getFromId(artistId: UUID): Flow<Artist?> = artistLocalDataSource.getFromId(
        artistId = artistId
    )

    override suspend fun getFromName(artistName: String): Artist? =
        artistLocalDataSource.getFromName(artistName = artistName)

    override suspend fun getAllFromName(artistsNames: List<String>): List<Artist> =
        artistLocalDataSource.getAllFromName(artistsNames)

    /**
     * Retrieves a flow of all Artist, sorted by name asc.
     */
    override fun getAll(): Flow<List<Artist>> =
        artistLocalDataSource.getAll()

    /**
     * Retrieves a flow of all ArtistWithMusics, sorted by name asc.
     */
    override fun getAllArtistWithMusics(): Flow<List<ArtistWithMusics>> =
        artistLocalDataSource.getAllArtistWithMusics()

    /**
     * Retrieves a flow of an ArtistWithMusics.
     */
    override fun getArtistWithMusics(artistId: UUID): Flow<ArtistWithMusics?> =
        artistLocalDataSource.getArtistWithMusics(
            artistId = artistId
        )

    override fun getArtistsOfMusic(musicId: UUID): Flow<List<Artist>> =
        artistLocalDataSource.getArtistsOfMusic(musicId)

}