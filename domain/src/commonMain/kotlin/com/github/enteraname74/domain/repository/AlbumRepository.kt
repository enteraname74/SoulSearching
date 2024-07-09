package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.model.AlbumWithMusics
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * Repository of an Album.
 */
interface AlbumRepository {
    /**
     * Update an album with new information.
     */
    suspend fun update(
        newAlbumWithArtistInformation: AlbumWithArtist
    )

    /**
     * Delete an album.
     */
    suspend fun delete(albumId: UUID)

    /**
     * Inserts a new Album.
     */
    suspend fun insert(album: Album)

    /**
     * Retrieves all Albums from an Artist as a flow.
     */
    fun getAlbumsOfArtist(artistId: UUID): Flow<List<Album>>

    /**
     * Retrieves an Album from its id.
     */
    fun getFromId(albumId: UUID): Flow<Album?>

    /**
     * Retrieves a flow of an AlbumWithMusics from an album's id.
     */
    fun getAlbumWithMusics(albumId: UUID): Flow<AlbumWithMusics?>

    /**
     * Retrieves a flow of all Album, sorted by name asc.
     */
    fun getAll(): Flow<List<Album>>

    /**
     * Retrieves a flow of all AlbumsWithMusics.
     */
    fun getAllAlbumsWithMusics(): Flow<List<AlbumWithMusics>>

    /**
     * Retrieves all AlbumsWithArtist.
     */
    suspend fun getAllAlbumsWithArtist(): List<AlbumWithArtist>
}