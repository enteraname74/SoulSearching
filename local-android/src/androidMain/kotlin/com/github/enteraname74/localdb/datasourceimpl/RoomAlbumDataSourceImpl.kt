package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.model.toAlbum
import com.github.enteraname74.localdb.model.toAlbumWithArtist
import com.github.enteraname74.localdb.model.toAlbumWithMusics
import com.github.enteraname74.localdb.model.toRoomAlbum
import com.github.enteraname74.soulsearching.repository.datasource.AlbumDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

/**
 * Implementation of the AlbumDataSource with Room's DAO.
 */
internal class RoomAlbumDataSourceImpl(
    private val appDatabase: AppDatabase
) : AlbumDataSource {
    override suspend fun upsert(album: Album) {
        appDatabase.albumDao.upsert(
            roomAlbum = album.toRoomAlbum()
        )
    }

    override suspend fun delete(album: Album) {
        appDatabase.albumDao.delete(
            roomAlbum = album.toRoomAlbum()
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

    override fun getAll(): Flow<List<Album>> {
        return appDatabase.albumDao.getAll().map { list ->
            list.map { it.toAlbum() }
        }
    }

    override fun getAllAlbumWithMusics(): Flow<List<AlbumWithMusics>> {
        return appDatabase.albumDao.getAllAlbumWithMusics().map { list ->
            list.map { it.toAlbumWithMusics() }
        }
    }

    override fun getAllAlbumsWithArtist(): Flow<List<AlbumWithArtist>> {
        return appDatabase.albumDao.getAllAlbumsWithArtist().map { list ->
            list.map { it.toAlbumWithArtist() }
        }
    }
}