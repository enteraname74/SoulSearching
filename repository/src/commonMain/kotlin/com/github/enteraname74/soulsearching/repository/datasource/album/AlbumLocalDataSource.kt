package com.github.enteraname74.soulsearching.repository.datasource.album

import com.github.enteraname74.domain.model.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Data source of an Album.
 */
interface AlbumLocalDataSource {

    /**
     * Inserts or updates a new Album.
     */
    suspend fun upsert(album: Album)

    suspend fun upsertAll(albums: List<Album>)

    /**
     * Deletes an Album.
     */
    suspend fun delete(album: Album)

    suspend fun deleteAll(ids: List<UUID>)
    suspend fun deleteAll(dataMode: DataMode)

    /**
     * Retrieves all Albums from an Artist as a flow.
     */
    fun getAlbumsOfArtist(artistId: UUID): Flow<List<Album>>

    fun getAlbumsWithMusicsOfArtist(artistId: UUID): Flow<List<AlbumWithMusics>>

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
    fun getAll(dataMode: DataMode): Flow<List<Album>>

    /**
     * Retrieves all musics from a list of ids.
     */
    suspend fun getAll(albumIds: List<UUID>): List<Album>

    /**
     * Retrieves a flow of all AlbumsWithMusics, sorted by name asc.
     */
    fun getAllAlbumWithMusics(dataMode: DataMode): Flow<List<AlbumWithMusics>>

    /**
     * Retrieves all AlbumsWithArtist.
     */
    fun getAllAlbumsWithArtist(dataMode: DataMode): Flow<List<AlbumWithArtist>>
}