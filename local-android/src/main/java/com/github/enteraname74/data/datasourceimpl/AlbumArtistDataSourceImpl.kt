package com.github.enteraname74.data.datasourceimpl

import com.github.enteraname74.data.AppDatabase
import com.github.enteraname74.data.model.toRoomAlbumArtist
import com.github.enteraname74.domain.datasource.AlbumArtistDataSource
import com.github.enteraname74.domain.model.AlbumArtist
import java.util.UUID
import javax.inject.Inject

/**
 * Implementation of the AlbumArtistDataSource with Room's DAO.
 */
internal class AlbumArtistDataSourceImpl @Inject constructor(
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