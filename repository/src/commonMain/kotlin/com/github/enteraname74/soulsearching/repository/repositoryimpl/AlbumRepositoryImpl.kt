package com.github.enteraname74.soulsearching.repository.repositoryimpl

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumPreview
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.soulsearching.repository.datasource.AlbumDataSource
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * Repository of an Album.
 */
class AlbumRepositoryImpl(
    private val albumDataSource: AlbumDataSource,
): AlbumRepository {

    override suspend fun delete(album: Album) {
        albumDataSource.delete(album = album)
    }

    override suspend fun deleteAll(ids: List<UUID>) {
        albumDataSource.deleteAll(ids = ids)
    }

    override suspend fun upsertAll(albums: List<Album>) {
        albumDataSource.upsertAll(albums)
    }

    override suspend fun getAlbumNamesContainingSearch(search: String): List<String> =
        albumDataSource.getAlbumNamesContainingSearch(search)

    override suspend fun upsert(album: Album) = albumDataSource.upsert(
        album = album
    )

    override fun getAlbumsOfArtist(artistId: UUID): Flow<List<Album>> = albumDataSource.getAlbumsOfArtist(
        artistId = artistId
    )

    override fun getAlbumsWithMusicsOfArtist(artistId: UUID): Flow<List<AlbumWithMusics>> =
        albumDataSource.getAlbumsWithMusicsOfArtist(artistId)

    override fun getFromId(albumId: UUID): Flow<Album?> = albumDataSource.getFromId(
        albumId = albumId
    )

    override fun getAlbumWithMusics(albumId: UUID): Flow<AlbumWithMusics?> =
        albumDataSource.getAlbumWithMusics(
            albumId = albumId
        )

    @Deprecated("Avoid fetching all album from DB because of performance issue")
    override fun getAll(): Flow<List<Album>> =
        albumDataSource.getAll()

    override fun getAllPaged(): Flow<PagingData<AlbumPreview>> =
        albumDataSource.getAllPaged()

    override fun getAllAlbumWithMusics(): Flow<List<AlbumWithMusics>> =
        albumDataSource.getAllAlbumWithMusics()

    override fun getAllFromQuickAccess(): Flow<List<AlbumPreview>> =
        albumDataSource.getAllFromQuickAccess()

    override suspend fun cleanAllMusicCovers() {
        albumDataSource.cleanAllMusicCovers()
    }
}