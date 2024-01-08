package com.github.enteraname74.repository

import com.github.enteraname74.datasource.AlbumArtistDataSource
import com.github.enteraname74.model.AlbumArtist
import jakarta.inject.Inject
import java.util.UUID

/**
 * Repository of an AlbumArtist.
 */
class AlbumArtistRepository @Inject constructor(
    private val albumArtistDataSource: AlbumArtistDataSource
) {
    /**
     * Insert or updates an AlbumArtist.
     */
    suspend fun insertAlbumIntoArtist(albumArtist: AlbumArtist) =
        albumArtistDataSource.insertAlbumIntoArtist(albumArtist = albumArtist)

    /**
     * Update the artist of an album.
     */
    suspend fun updateArtistOfAlbum(albumId: UUID, newArtistId: UUID) =
        albumArtistDataSource.updateArtistOfAlbum(
            albumId = albumId,
            newArtistId = newArtistId
        )

    /**
     * Delete an album from an artist.
     */
    suspend fun deleteAlbumFromArtist(albumId: UUID) = albumArtistDataSource.deleteAlbumFromArtist(
        albumId = albumId
    )
}