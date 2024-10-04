package com.github.enteraname74.soulsearching.localdesktop.datasourceimpl

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.soulsearching.localdesktop.dao.AlbumDao
import com.github.enteraname74.soulsearching.repository.datasource.AlbumDataSource
import kotlinx.coroutines.flow.Flow
import java.util.*

internal class AlbumDataSourceImpl(
    private val albumDao: AlbumDao,
) : AlbumDataSource {
    override suspend fun upsert(album: Album) {
        albumDao.upsert(album = album)
    }

    override suspend fun upsertAll(albums: List<Album>) {
        albumDao.upsertAll(
            albums = albums,
        )
    }

    override suspend fun delete(album: Album) {
        albumDao.delete(album = album)
    }

    override fun getAlbumsOfArtist(artistId: UUID): Flow<List<Album>> = albumDao.getAlbumsOfArtist(
        artistId = artistId
    )

    override fun getFromId(albumId: UUID): Flow<Album?> =
        albumDao.getFromId(albumId = albumId)

    override fun getAll(): Flow<List<Album>> =
        albumDao.getAll()

    override fun getAllAlbumWithMusics(): Flow<List<AlbumWithMusics>> =
        albumDao.getAllAlbumsWithMusics()

    override fun getAlbumWithMusics(albumId: UUID): Flow<AlbumWithMusics?> =
        albumDao.getAlbumWithMusics(
            albumId = albumId
        )

    override fun getAllAlbumsWithArtist(): Flow<List<AlbumWithArtist>> =
        albumDao.getAllAlbumsWithArtist()
}