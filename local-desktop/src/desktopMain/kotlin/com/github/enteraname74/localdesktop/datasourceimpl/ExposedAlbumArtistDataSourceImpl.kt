package com.github.enteraname74.localdesktop.datasourceimpl

import com.github.enteraname74.domain.datasource.AlbumArtistDataSource
import com.github.enteraname74.domain.model.AlbumArtist
import java.util.UUID

/**
 * Implementation of the AlbumArtistDataSource with Exposed.
 */
class ExposedAlbumArtistDataSourceImpl: AlbumArtistDataSource {
    override suspend fun insertAlbumIntoArtist(albumArtist: AlbumArtist) {}

    override suspend fun updateArtistOfAlbum(albumId: UUID, newArtistId: UUID) {}

    override suspend fun deleteAlbumFromArtist(albumId: UUID) {}
}