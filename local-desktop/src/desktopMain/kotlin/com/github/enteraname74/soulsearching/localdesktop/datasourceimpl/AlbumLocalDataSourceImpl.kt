package com.github.enteraname74.soulsearching.localdesktop.datasourceimpl

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.soulsearching.localdesktop.dao.AlbumDao
import com.github.enteraname74.soulsearching.repository.datasource.album.AlbumLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

internal class AlbumLocalDataSourceImpl(
    private val albumDao: AlbumDao,
) : AlbumLocalDataSource {
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

    override suspend fun deleteAll(ids: List<UUID>) {
        albumDao.deleteAll(ids = ids)
    }

    override suspend fun deleteAll(dataMode: DataMode) {
        albumDao.deleteAll(dataMode = dataMode.value)
    }

    override fun getAlbumsWithMusicsOfArtist(artistId: UUID): Flow<List<AlbumWithMusics>> =
        albumDao.getAlbumsWithMusicsOfArtist(artistId)

    override fun getAlbumsOfArtist(artistId: UUID): Flow<List<Album>> = albumDao.getAlbumsOfArtist(
        artistId = artistId
    )

    override fun getFromId(albumId: UUID): Flow<Album?> =
        albumDao.getFromId(albumId = albumId)

    override fun getAll(dataMode: DataMode): Flow<List<Album>> =
        albumDao.getAll(dataMode.value)

    override fun getAllAlbumWithMusics(dataMode: DataMode): Flow<List<AlbumWithMusics>> =
        albumDao.getAllAlbumsWithMusics(dataMode.value)

    override fun getAlbumWithMusics(albumId: UUID): Flow<AlbumWithMusics?> =
        albumDao.getAlbumWithMusics(
            albumId = albumId
        )

    override fun getAllAlbumsWithArtist(dataMode: DataMode): Flow<List<AlbumWithArtist>> =
        albumDao.getAllAlbumsWithMusics(dataMode.value).map { list ->
            list.map { it.toAlbumWithArtist() }
        }
}