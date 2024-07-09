package com.github.enteraname74.soulsearching.repository.datasource

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.model.AlbumWithMusics
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Data source of an Album.
 */
interface AlbumDataSource {

    /**
     * Inserts or updates a new Album.
     */
    suspend fun upsert(album: Album)

    /**
     * Deletes an Album.
     */
    suspend fun delete(album: Album)

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
     * Retrieves a flow of all Album, sorted bu name asc.
     */
    fun getAll(): Flow<List<Album>>

    /**
     * Retrieves a flow of all AlbumsWithMusics, sorted by name asc.
     */
    fun getAllAlbumWithMusics(): Flow<List<AlbumWithMusics>>

    /**
     * Retrieves all AlbumsWithArtist.
     */
    suspend fun getAllAlbumsWithArtist(): List<AlbumWithArtist>
}