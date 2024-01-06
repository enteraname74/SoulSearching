package com.github.enteraname74.domain.datasource

import com.github.enteraname74.domain.model.AlbumArtist
import java.util.UUID

/**
 * Data source of an AlbumArtist.
 */
interface AlbumArtistDataSource {

    /**
     * Insert or updates an AlbumArtist.
     */
    suspend fun insertAlbumIntoArtist(albumArtist: AlbumArtist)

    /**
     * Update the artist of an album.
     */
    suspend fun updateArtistOfAlbum(albumId: UUID, newArtistId: UUID)

    /**
     * Delete an album from an artist.
     */
    suspend fun deleteAlbumFromArtist(albumId: UUID)
}