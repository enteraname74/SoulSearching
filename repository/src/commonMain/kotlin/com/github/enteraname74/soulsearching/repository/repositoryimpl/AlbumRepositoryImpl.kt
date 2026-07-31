package com.github.enteraname74.soulsearching.repository.repositoryimpl

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumPreview
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.soulsearching.repository.datasource.AlbumDataSource
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

/**
 * Repository of an Album.
 */
class AlbumRepositoryImpl(
    private val albumDataSource: AlbumDataSource,
): AlbumRepository {

    override suspend fun delete(album: Album) {
        albumDataSource.delete(album = album)
    }

    override suspend fun deleteAll(ids: List<Uuid>) {
        albumDataSource.deleteAll(ids = ids)
    }

    override suspend fun deleteAllEmpty() {
        albumDataSource.deleteAllEmpty()
    }

    override suspend fun upsertAll(albums: List<Album>) {
        albumDataSource.upsertAll(albums)
    }

    override suspend fun getAlbumNamesContainingSearch(search: String): List<String> =
        albumDataSource.getAlbumNamesContainingSearch(search)

    override suspend fun upsert(album: Album) = albumDataSource.upsert(
        album = album
    )

    override fun getAlbumsOfArtist(artistId: Uuid): Flow<List<Album>> = albumDataSource.getAlbumsOfArtist(
        artistId = artistId
    )

    override fun getAlbumsWithMusicsOfArtist(artistId: Uuid): Flow<List<AlbumWithMusics>> =
        albumDataSource.getAlbumsWithMusicsOfArtist(artistId)

    override fun getFromId(albumId: Uuid): Flow<Album?> = albumDataSource.getFromId(
        albumId = albumId
    )

    override suspend fun getFromRemoteId(remoteId: Uuid): Album? =
        albumDataSource.getFromRemoteId(remoteId)

    override fun getFromIds(albumIds: List<Uuid>): Flow<List<AlbumWithMusics>> =
        albumDataSource.getFromIds(albumIds)

    override fun getAlbumWithMusics(albumId: Uuid): Flow<AlbumWithMusics?> =
        albumDataSource.getAlbumWithMusics(
            albumId = albumId
        )

    override fun getAllPaged(): Flow<PagingData<AlbumPreview>> =
        albumDataSource.getAllPaged()

    override suspend fun getAll(page: Int, pageSize: Int): List<AlbumPreview> =
        albumDataSource.getAll(page = page, pageSize = pageSize)

    override fun getAllFromQuickAccess(): Flow<List<AlbumPreview>> =
        albumDataSource.getAllFromQuickAccess()

    override suspend fun cleanAllCovers() {
        albumDataSource.cleanAllCovers()
    }

    override suspend fun getDuplicatedAlbum(
        albumId: Uuid,
        albumName: String,
        artistId: Uuid
    ): Album? =
        albumDataSource.getDuplicatedAlbum(
            albumId = albumId,
            albumName = albumName,
            artistId = artistId,
        )

    override suspend fun getFromInformation(
        albumName: String,
        artistName: String
    ): Album? =
        albumDataSource.getFromInformation(
            albumName = albumName,
            artistName = artistName,
        )

    override suspend fun getFromArtistId(
        albumName: String,
        artistId: Uuid
    ): Album? =
        albumDataSource.getFromArtistId(
            albumName = albumName,
            artistId = artistId,
        )

    override fun getMostListened(): Flow<List<AlbumPreview>> =
        albumDataSource.getMostListened()

    override fun getAlbumPreview(albumId: Uuid): Flow<AlbumPreview?> =
        albumDataSource.getAlbumPreview(albumId)

    override fun searchAll(search: String): Flow<List<AlbumPreview>> =
        albumDataSource.searchAll(search)

    override suspend fun getAlbumsOfArtistName(artistName: String): List<AlbumWithMusics> =
        albumDataSource.getAlbumsOfArtistName(artistName)
}
