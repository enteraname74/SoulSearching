package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.soulsearching.repository.datasource.album.AlbumLocalDataSource
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * Repository of an Album.
 */
class AlbumRepositoryImpl(
    private val albumLocalDataSource: AlbumLocalDataSource,
): AlbumRepository {

    override suspend fun delete(album: Album) {
        albumLocalDataSource.delete(album = album)
    }

    override suspend fun deleteAll(ids: List<UUID>) {
        albumLocalDataSource.deleteAll(ids = ids)
    }

    override suspend fun upsertAll(albums: List<Album>) {
        albumLocalDataSource.upsertAll(albums)
    }

    override suspend fun upsert(album: Album) = albumLocalDataSource.upsert(
        album = album
    )

    override fun getAlbumsOfArtist(artistId: UUID): Flow<List<Album>> = albumLocalDataSource.getAlbumsOfArtist(
        artistId = artistId
    )

    override fun getAlbumsWithMusicsOfArtist(artistId: UUID): Flow<List<AlbumWithMusics>> =
        albumLocalDataSource.getAlbumsWithMusicsOfArtist(artistId)

    override fun getFromId(albumId: UUID): Flow<Album?> = albumLocalDataSource.getFromId(
        albumId = albumId
    )

    override fun getAlbumWithMusics(albumId: UUID): Flow<AlbumWithMusics?> =
        albumLocalDataSource.getAlbumWithMusics(
            albumId = albumId
        )

    override fun getAll(): Flow<List<Album>> =
        albumLocalDataSource.getAll()

    override fun getAllAlbumWithMusics(): Flow<List<AlbumWithMusics>> =
        albumLocalDataSource.getAllAlbumWithMusics()

    override fun getAllAlbumsWithArtist(): Flow<List<AlbumWithArtist>> =
        albumLocalDataSource.getAllAlbumsWithArtist()
}