package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithArtist
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

    override suspend fun upsert(album: Album) = albumDataSource.upsert(
        album = album
    )

    override fun getAlbumsOfArtist(artistId: UUID): Flow<List<Album>> = albumDataSource.getAlbumsOfArtist(
        artistId = artistId
    )

    override fun getFromId(albumId: UUID): Flow<Album?> = albumDataSource.getFromId(
        albumId = albumId
    )

    override fun getAlbumWithMusics(albumId: UUID): Flow<AlbumWithMusics?> =
        albumDataSource.getAlbumWithMusics(
            albumId = albumId
        )

    override fun getAll(): Flow<List<Album>> =
        albumDataSource.getAll()

    override fun getAllAlbumWithMusics(): Flow<List<AlbumWithMusics>> =
        albumDataSource.getAllAlbumWithMusics()

    override fun getAllAlbumsWithArtist(): Flow<List<AlbumWithArtist>> =
        albumDataSource.getAllAlbumsWithArtist()
}