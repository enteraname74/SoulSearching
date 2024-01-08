package com.github.enteraname74.datasource

import com.github.enteraname74.model.Album
import com.github.enteraname74.model.AlbumWithArtist
import com.github.enteraname74.model.AlbumWithMusics
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Data source of an Album.
 */
interface AlbumDataSource {

    /**
     * Inserts or updates a new Album.
     */
    suspend fun insertAlbum(album: Album)

    /**
     * Deletes an Album.
     */
    suspend fun deleteAlbum(album: Album)

    /**
     * Retrieves all Albums from an Artist.
     */
    suspend fun getAllAlbumsFromArtist(artistId: UUID) : List<Album>

    /**
     * Retrieves an Album from its id.
     */
    suspend fun getAlbumFromId(albumId: UUID): Album?
    
    /**
     * Retrieves a flow of an AlbumWithMusics from an album's id.
     */
    fun getAlbumWithMusicsAsFlow(albumId: UUID): Flow<AlbumWithMusics?>

    /**
     * Retrieves an AlbumWithMusics from an album's id.
     */
    suspend fun getAlbumWithMusics(albumId: UUID): AlbumWithMusics

    /**
     * Retrieves a flow of all Album, sorted bu name asc.
     */
    fun getAllAlbumsSortByNameAscAsFlow(): Flow<List<Album>>

    /**
     * Retrieves a flow of all AlbumsWithMusics, sorted by name asc.
     */
    fun getAllAlbumsWithMusicsSortByNameAscAsFlow(): Flow<List<AlbumWithMusics>>

    /**
     * Retrieves a flow of all AlbumsWithMusics, sorted by name desc.
     */
    fun getAllAlbumsWithMusicsSortByNameDescAsFlow(): Flow<List<AlbumWithMusics>>

    /**
     * Retrieves a flow of all AlbumsWithMusics, sorted by added date asc.
     */
    fun getAllAlbumsWithMusicsSortByAddedDateAscAsFlow(): Flow<List<AlbumWithMusics>>

    /**
     * Retrieves a flow of all AlbumsWithMusics, sorted by date desc.
     */
    fun getAllAlbumsWithMusicsSortByAddedDateDescAsFlow(): Flow<List<AlbumWithMusics>>

    /**
     * Retrieves a flow of all AlbumsWithMusics, sorted by nb played asc.
     */
    fun getAllAlbumsWithMusicsSortByNbPlayedAscAsFlow(): Flow<List<AlbumWithMusics>>

    /**
     * Retrieves a flow of all AlbumsWithMusics, sorted by nb played desc.
     */
    fun getAllAlbumsWithMusicsSortByNbPlayedDescAsFlow(): Flow<List<AlbumWithMusics>>

    /**
     * Retrieves all AlbumsWithArtist.
     */
    suspend fun getAllAlbumsWithArtist(): List<AlbumWithArtist>

    /**
     * Retrieves all AlbumsWithMusics.
     */
    suspend fun getAllAlbumsWithMusics(): List<AlbumWithMusics>

    /**
     * Retrieves a flow of all albums from the quick access.
     */
    fun getAllAlbumWithArtistFromQuickAccessAsFlow(): Flow<List<AlbumWithArtist>>

    /**
     * Tries to find a corresponding album from its name and its artist's id.
     */
    suspend fun getCorrespondingAlbum(albumName: String, artistId: UUID): Album?

    /**
     * Tries to find a duplicate album.
     */
    suspend fun getPossibleDuplicateAlbum(albumId: UUID, albumName: String, artistId: UUID): Album?

    /**
     * Updates the cover of an album.
     */
    suspend fun updateAlbumCover(newCoverId : UUID, albumId : UUID)

    /**
     * Updates the quick access status of an album.
     */
    suspend fun updateQuickAccessState(newQuickAccessState: Boolean, albumId: UUID)

    /**
     * Retrieves the number of albums sharing the same cover.
     */
    suspend fun getNumberOfAlbumsWithCoverId(coverId : UUID) : Int

    /**
     * Retrieves the number of time an Album has been played.
     */
    suspend fun getNbPlayedOfAlbum(albumId: UUID): Int

    /**
     * Update the total of played time of an Album.
     */
    suspend fun updateNbPlayed(newNbPlayed: Int, albumId: UUID)
}