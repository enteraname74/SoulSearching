package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.domain.datasource.AlbumArtistDataSource
import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.model.toRoomAlbumArtist
import com.github.enteraname74.domain.model.AlbumArtist
import java.util.UUID

/**
 * Implementation of the AlbumArtistDataSource with Room's DAO.
 */
internal class RoomAlbumArtistDataSourceImpl(
    private val appDatabase: AppDatabase
) : AlbumArtistDataSource {
    override suspend fun insertAlbumIntoArtist(albumArtist: AlbumArtist) {
        appDatabase.albumArtistDao.insertAlbumIntoArtist(
            roomAlbumArtist = albumArtist.toRoomAlbumArtist()
        )
    }

    override suspend fun updateArtistOfAlbum(albumId: UUID, newArtistId: UUID) {
        appDatabase.albumArtistDao.updateArtistOfAlbum(
            albumId = albumId,
            newArtistId = newArtistId
        )
    }

    override suspend fun deleteAlbumFromArtist(albumId: UUID) {
        appDatabase.albumArtistDao.deleteAlbumFromArtist(albumId = albumId)
    }
}