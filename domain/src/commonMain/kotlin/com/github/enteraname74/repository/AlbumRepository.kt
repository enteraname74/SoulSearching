package com.github.enteraname74.repository

import com.github.enteraname74.datasource.AlbumDataSource
import com.github.enteraname74.model.Album
import com.github.enteraname74.model.AlbumWithArtist
import com.github.enteraname74.model.AlbumWithMusics
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Repository of an Album.
 */
class AlbumRepository(
    private val albumDataSource: AlbumDataSource
) {
    /**
     * Inserts or updates a new Album.
     */
    suspend fun insertAlbum(album: Album) = albumDataSource.insertAlbum(
        album = album
    )

    /**
     * Deletes an Album.
     */
    suspend fun deleteAlbum(album: Album) = albumDataSource.deleteAlbum(
        album = album
    )

    /**
     * Retrieves all Albums from an Artist.
     */
    suspend fun getAllAlbumsFromArtist(artistId: UUID): List<Album> =
        albumDataSource.getAllAlbumsFromArtist(
            artistId = artistId
        )

    /**
     * Retrieves an Album from its id.
     */
    suspend fun getAlbumFromId(albumId: UUID): Album? = albumDataSource.getAlbumFromId(
        albumId = albumId
    )

    /**
     * Retrieves a flow of an AlbumWithMusics from an album's id.
     */
    fun getAlbumWithMusicsAsFlow(albumId: UUID): Flow<AlbumWithMusics?> =
        albumDataSource.getAlbumWithMusicsAsFlow(
            albumId = albumId
        )

    /**
     * Retrieves an AlbumWithMusics from an album's id.
     */
    suspend fun getAlbumWithMusics(albumId: UUID): AlbumWithMusics =
        albumDataSource.getAlbumWithMusics(
            albumId = albumId
        )

    /**
     * Retrieves a flow of all Album, sorted bu name asc.
     */
    fun getAllAlbumsSortByNameAscAsFlow(): Flow<List<Album>> =
        albumDataSource.getAllAlbumsSortByNameAscAsFlow()

    /**
     * Retrieves a flow of all AlbumsWithMusics, sorted by name asc.
     */
    fun getAllAlbumsWithMusicsSortByNameAscAsFlow(): Flow<List<AlbumWithMusics>> =
        albumDataSource.getAllAlbumsWithMusicsSortByNameDescAsFlow()

    /**
     * Retrieves a flow of all AlbumsWithMusics, sorted by name desc.
     */
    fun getAllAlbumsWithMusicsSortByNameDescAsFlow(): Flow<List<AlbumWithMusics>> =
        albumDataSource.getAllAlbumsWithMusicsSortByNameDescAsFlow()

    /**
     * Retrieves a flow of all AlbumsWithMusics, sorted by added date asc.
     */
    fun getAllAlbumsWithMusicsSortByAddedDateAscAsFlow(): Flow<List<AlbumWithMusics>> =
        albumDataSource.getAllAlbumsWithMusicsSortByAddedDateAscAsFlow()

    /**
     * Retrieves a flow of all AlbumsWithMusics, sorted by date desc.
     */
    fun getAllAlbumsWithMusicsSortByAddedDateDescAsFlow(): Flow<List<AlbumWithMusics>> =
        albumDataSource.getAllAlbumsWithMusicsSortByAddedDateDescAsFlow()

    /**
     * Retrieves a flow of all AlbumsWithMusics, sorted by nb played asc.
     */
    fun getAllAlbumsWithMusicsSortByNbPlayedAscAsFlow(): Flow<List<AlbumWithMusics>> =
        albumDataSource.getAllAlbumsWithMusicsSortByNbPlayedAscAsFlow()

    /**
     * Retrieves a flow of all AlbumsWithMusics, sorted by nb played desc.
     */
    fun getAllAlbumsWithMusicsSortByNbPlayedDescAsFlow(): Flow<List<AlbumWithMusics>> =
        albumDataSource.getAllAlbumsWithMusicsSortByNbPlayedDescAsFlow()

    /**
     * Retrieves all AlbumsWithArtist.
     */
    suspend fun getAllAlbumsWithArtist(): List<AlbumWithArtist> =
        albumDataSource.getAllAlbumsWithArtist()

    /**
     * Retrieves all AlbumsWithMusics.
     */
    suspend fun getAllAlbumsWithMusics(): List<AlbumWithMusics> =
        albumDataSource.getAllAlbumsWithMusics()

    /**
     * Retrieves a flow of all albums from the quick access.
     */
    fun getAllAlbumWithArtistFromQuickAccessAsFlow(): Flow<List<AlbumWithArtist>> =
        albumDataSource.getAllAlbumWithArtistFromQuickAccessAsFlow()

    /**
     * Tries to find a corresponding album from its name and its artist's id.
     */
    suspend fun getCorrespondingAlbum(albumName: String, artistId: UUID): Album? =
        albumDataSource.getCorrespondingAlbum(
            albumName = albumName,
            artistId = artistId
        )

    /**
     * Tries to find a duplicate album.
     */
    suspend fun getPossibleDuplicateAlbum(
        albumId: UUID,
        albumName: String,
        artistId: UUID
    ): Album? = albumDataSource.getPossibleDuplicateAlbum(
        albumId = albumId,
        albumName = albumName,
        artistId = artistId
    )

    /**
     * Updates the cover of an album.
     */
    suspend fun updateAlbumCover(newCoverId: UUID, albumId: UUID) =
        albumDataSource.updateAlbumCover(
            newCoverId = newCoverId,
            albumId = albumId
        )

    /**
     * Updates the quick access status of an album.
     */
    suspend fun updateQuickAccessState(newQuickAccessState: Boolean, albumId: UUID) =
        albumDataSource.updateQuickAccessState(
            newQuickAccessState = newQuickAccessState,
            albumId = albumId
        )

    /**
     * Retrieves the number of albums sharing the same cover.
     */
    suspend fun getNumberOfAlbumsWithCoverId(coverId: UUID): Int =
        albumDataSource.getNumberOfAlbumsWithCoverId(
            coverId = coverId
        )

    /**
     * Retrieves the number of time an Album has been played.
     */
    suspend fun getNbPlayedOfAlbum(albumId: UUID): Int =
        albumDataSource.getNbPlayedOfAlbum(albumId = albumId)

    /**
     * Update the total of played time of an Album.
     */
    suspend fun updateNbPlayed(newNbPlayed: Int, albumId: UUID) = albumDataSource.updateNbPlayed(
        newNbPlayed = newNbPlayed,
        albumId = albumId
    )
}