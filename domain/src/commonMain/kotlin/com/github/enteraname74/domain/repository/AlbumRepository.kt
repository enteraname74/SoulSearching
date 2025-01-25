package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.model.*
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * Repository of an Album.
 */
interface AlbumRepository {
    /**
     * Delete an album.
     */
    suspend fun delete(album: Album): SoulResult<String>
    suspend fun deleteAll(ids: List<UUID>): SoulResult<String>
    suspend fun deleteAll(dataMode: DataMode)

    /**
     * Inserts a new Album.
     * @param artist the name of the artist of the album. Used by the cloud to update the artist of the album.
     */
    suspend fun upsert(
        album: Album,
        artist: String = "",
    ): SoulResult<String>
    suspend fun upsertAll(albums: List<Album>)

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
    fun getAll(): Flow<List<Album>>

    /**
     * Retrieves a flow of all AlbumsWithMusics.
     */
    fun getAllAlbumWithMusics(): Flow<List<AlbumWithMusics>>

    /**
     * Retrieves all AlbumsWithArtist.
     */
    fun getAllAlbumsWithArtist(): Flow<List<AlbumWithArtist>>

    /**
     * Synchronize remote albums of the users with the cloud
     */
    suspend fun syncWithCloud(): SoulResult<Unit>
}