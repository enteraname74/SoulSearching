package com.github.enteraname74.soulsearching.localdesktop.datasourceimpl

import com.github.enteraname74.domain.model.AlbumArtist
import com.github.enteraname74.soulsearching.localdesktop.dao.AlbumArtistDao
import com.github.enteraname74.soulsearching.repository.datasource.AlbumArtistDataSource
import java.util.*

internal class AlbumArtistDataSourceImpl(
    private val albumArtistDao: AlbumArtistDao
) : AlbumArtistDataSource {
    override suspend fun upsert(albumArtist: AlbumArtist) =
        albumArtistDao.upsertAlbumIntoArtist(
            albumArtist = albumArtist
        )

    override suspend fun update(albumId: UUID, newArtistId: UUID) =
        albumArtistDao.updateArtistOfAlbum(
            albumId = albumId,
            newArtistId = newArtistId
        )

    override suspend fun delete(albumId: UUID) =
        albumArtistDao.deleteAlbumFromArtist(albumId = albumId)
}