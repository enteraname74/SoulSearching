package com.github.enteraname74.localdesktop.daoimpl

import com.github.enteraname74.domain.model.AlbumArtist
import com.github.enteraname74.localdesktop.dao.AlbumArtistDao
import java.util.UUID

/**
 * Implementation of the AlbumArtistDao for Exposed.
 */
class ExposedAlbumArtistDaoImpl: AlbumArtistDao {
    override suspend fun insertAlbumIntoArtist(albumArtist: AlbumArtist) {
        TODO("Not yet implemented")
    }

    override suspend fun updateArtistOfAlbum(albumId: UUID, newArtistId: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlbumFromArtist(albumId: UUID) {
        TODO("Not yet implemented")
    }
}