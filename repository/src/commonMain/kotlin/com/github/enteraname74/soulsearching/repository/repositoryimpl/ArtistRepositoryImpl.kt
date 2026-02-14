package com.github.enteraname74.soulsearching.repository.repositoryimpl

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.ArtistPreview
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.soulsearching.repository.datasource.ArtistDataSource
import kotlinx.coroutines.flow.Flow
import java.util.UUID

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

    override suspend fun getArtistNamesContainingSearch(search: String): List<String> =
        artistDataSource.getArtistNamesContainingSearch(search)

    /**
     * Retrieves an Artist from its id.
     */
    override fun getFromId(artistId: UUID): Flow<Artist?> = artistDataSource.getFromId(
        artistId = artistId
    )

    override suspend fun getFromName(artistName: String): Artist? =
        artistDataSource.getFromName(artistName = artistName)

    override suspend fun getAllFromName(artistsNames: List<String>): List<Artist> =
        artistDataSource.getAllFromName(artistsNames)

    override suspend fun toggleCoverFolderMode(isActivated: Boolean) {
        artistDataSource.toggleCoverFolderMode(isActivated)
    }

    /**
     * Retrieves a flow of all Artist, sorted by name asc.
     */
    @Deprecated("Avoid fetching all artist from DB because of performance issue")
    override fun getAll(): Flow<List<Artist>> =
        artistDataSource.getAll()

    override fun getAllPaged(): Flow<PagingData<ArtistPreview>> =
        artistDataSource.getAllPaged()

    /**
     * Retrieves a flow of an ArtistWithMusics.
     */
    override fun getArtistWithMusics(artistId: UUID): Flow<ArtistWithMusics?> =
        artistDataSource.getArtistWithMusics(
            artistId = artistId
        )

    override fun getArtistsOfMusic(musicId: UUID): Flow<List<Artist>> =
        artistDataSource.getArtistsOfMusic(musicId = musicId)

    override fun getAllFromQuickAccess(): Flow<List<ArtistPreview>> =
        artistDataSource.getAllFromQuickAccess()

    override suspend fun getDuplicatedArtist(
        artistId: UUID,
        artistName: String
    ): ArtistWithMusics? =
        artistDataSource.getDuplicatedArtist(
            artistId = artistId,
            artistName = artistName,
        )

    override fun getArtistsWistMostMusics(): Flow<List<ArtistPreview>> =
        artistDataSource.getArtistsWistMostMusics()

    override suspend fun cleanAllCovers() {
        artistDataSource.cleanAllCovers()
    }

    override fun getMostListened(): Flow<List<ArtistPreview>> =
        artistDataSource.getMostListened()

    override fun getArtistPreview(artistId: UUID): Flow<ArtistPreview?> =
        artistDataSource.getArtistPreview(artistId)
}