package com.github.enteraname74.soulsearching.repository.datasource

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumPreview
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

    suspend fun upsertAll(albums: List<Album>)

    /**
     * Deletes an Album.
     */
    suspend fun delete(album: Album)

    suspend fun deleteAll(ids: List<UUID>)

    suspend fun getAlbumNamesContainingSearch(search: String): List<String>

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

    fun getAllPaged(): Flow<PagingData<AlbumPreview>>

    fun getAllFromQuickAccess(): Flow<List<AlbumPreview>>

    suspend fun cleanAllCovers()

    suspend fun getDuplicatedAlbum(
        albumId: UUID,
        albumName: String,
        artistId: UUID
    ): Album?

    suspend fun getFromInformation(
        albumName: String,
        artistName: String,
    ): Album?

    suspend fun getFromArtistId(
        albumName: String,
        artistId: UUID,
    ): Album?

    fun getMostListened(): Flow<List<AlbumPreview>>

    suspend fun getAlbumsOfArtistName(artistName: String): List<AlbumWithMusics>
}