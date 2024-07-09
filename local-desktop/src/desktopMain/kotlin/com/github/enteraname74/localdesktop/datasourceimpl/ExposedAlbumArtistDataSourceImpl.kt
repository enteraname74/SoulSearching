package com.github.enteraname74.localdesktop.datasourceimpl

import com.github.enteraname74.domain.datasource.AlbumArtistDataSource
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumArtist
import com.github.enteraname74.localdesktop.dao.AlbumArtistDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

/**
 * Implementation of the AlbumArtistDataSource with Exposed.
 */
internal class ExposedAlbumArtistDataSourceImpl(
    private val albumArtistDao: AlbumArtistDao
) : AlbumArtistDataSource {
    override suspend fun insert(albumArtist: AlbumArtist) =
        albumArtistDao.insertAlbumIntoArtist(
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