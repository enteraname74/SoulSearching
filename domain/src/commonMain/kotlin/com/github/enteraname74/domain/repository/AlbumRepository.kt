package com.github.enteraname74.domain.repository

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumPreview
import com.github.enteraname74.domain.model.AlbumWithMusics
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

/**
 * Repository of an Album.
 */
interface AlbumRepository {
    suspend fun delete(album: Album)

    suspend fun deleteAll(ids: List<Uuid>)

    suspend fun deleteAllEmpty()

    /**
     * Inserts a new Album.
     */
    suspend fun upsert(album: Album)

    suspend fun upsertAll(albums: List<Album>)

    suspend fun getAlbumNamesContainingSearch(search: String): List<String>

    /**
     * Retrieves all Albums from an Artist as a flow.
     */
    fun getAlbumsOfArtist(artistId: Uuid): Flow<List<Album>>

    fun getAlbumsWithMusicsOfArtist(artistId: Uuid): Flow<List<AlbumWithMusics>>

    /**
     * Retrieves an Album from its id.
     */
    fun getFromId(albumId: Uuid): Flow<Album?>

    suspend fun getFromRemoteId(remoteId: Uuid): Album?

    fun getFromIds(albumIds: List<Uuid>): Flow<List<AlbumWithMusics>>

    /**
     * Retrieves a flow of an AlbumWithMusics from an album's id.
     */
    fun getAlbumWithMusics(albumId: Uuid): Flow<AlbumWithMusics?>

    fun getAllPaged(): Flow<PagingData<AlbumPreview>>

    suspend fun getAll(page: Int, pageSize: Int): List<AlbumPreview>

    fun getAllFromQuickAccess(): Flow<List<AlbumPreview>>

    suspend fun cleanAllCovers()

    suspend fun getDuplicatedAlbum(
        albumId: Uuid,
        albumName: String,
        artistId: Uuid
    ): Album?

    suspend fun getFromInformation(
        albumName: String,
        artistName: String,
    ): Album?

    suspend fun getFromArtistId(
        albumName: String,
        artistId: Uuid,
    ): Album?

    fun getMostListened(): Flow<List<AlbumPreview>>

    fun getAlbumPreview(albumId: Uuid): Flow<AlbumPreview?>

    fun searchAll(search: String): Flow<List<AlbumPreview>>

    suspend fun getAlbumsOfArtistName(artistName: String): List<AlbumWithMusics>
}
