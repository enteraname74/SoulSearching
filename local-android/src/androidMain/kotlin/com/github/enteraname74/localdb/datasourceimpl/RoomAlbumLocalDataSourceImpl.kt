package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.model.toAlbum
import com.github.enteraname74.localdb.model.toAlbumWithMusics
import com.github.enteraname74.localdb.model.toRoomAlbum
import com.github.enteraname74.soulsearching.repository.datasource.album.AlbumLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

/**
 * Implementation of the AlbumDataSource with Room's DAO.
 */
internal class RoomAlbumLocalDataSourceImpl(
    private val appDatabase: AppDatabase
) : AlbumLocalDataSource {
    override suspend fun upsert(album: Album) {
        appDatabase.albumDao.upsert(
            roomAlbum = album.toRoomAlbum()
        )
    }

    override suspend fun upsertAll(albums: List<Album>) {
        appDatabase.albumDao.upsertAll(albums.map { it.toRoomAlbum() })
    }

    override suspend fun delete(album: Album) {
        appDatabase.albumDao.delete(
            roomAlbum = album.toRoomAlbum()
        )
    }

    override suspend fun deleteAll(ids: List<UUID>) {
        appDatabase.albumDao.deleteAll(
            ids = ids,
        )
    }

    override suspend fun deleteAll(dataMode: DataMode) {
        appDatabase.albumDao.deleteAll(
            dataMode = dataMode.value,
        )
    }

    override fun getAlbumsOfArtist(artistId: UUID): Flow<List<Album>> {
        return appDatabase.albumDao.getAllAlbumsFromArtist(
            artistId = artistId
        ).map { list ->
            list.map {
                it.toAlbum()
            }
        }
    }

    override fun getAlbumsWithMusicsOfArtist(artistId: UUID): Flow<List<AlbumWithMusics>> =
        appDatabase.albumDao.getAllAlbumsWithMusicsFromArtist(
            artistId = artistId
        ).map { list ->
            list.map {
                it.toAlbumWithMusics()
            }
        }


    override fun getFromId(albumId: UUID): Flow<Album?> {
        return appDatabase.albumDao.getFromId(
            albumId = albumId
        ).map { it?.toAlbum() }
    }

    override fun getAlbumWithMusics(albumId: UUID): Flow<AlbumWithMusics?> {
        return appDatabase.albumDao.getAlbumWithMusics(
            albumId = albumId
        ).map { it?.toAlbumWithMusics() }
    }

    override fun getAll(dataMode: DataMode): Flow<List<Album>> {
        return appDatabase.albumDao.getAll(dataMode.value).map { list ->
            list.map { it.toAlbum() }
        }
    }

    override fun getAllAlbumWithMusics(dataMode: DataMode): Flow<List<AlbumWithMusics>> {
        return appDatabase.albumDao.getAllAlbumWithMusics(dataMode.value).map { list ->
            list.map { it.toAlbumWithMusics() }
        }
    }

    override fun getAllAlbumsWithArtist(dataMode: DataMode): Flow<List<AlbumWithArtist>> {
        return appDatabase.albumDao.getAllAlbumWithMusics(dataMode.value).map { list ->
            list.map { it.toAlbumWithArtist() }
        }
    }
}