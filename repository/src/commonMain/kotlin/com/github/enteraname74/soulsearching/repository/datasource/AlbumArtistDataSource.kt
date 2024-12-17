package com.github.enteraname74.soulsearching.repository.datasource

import com.github.enteraname74.domain.model.AlbumArtist
import java.util.UUID

/**
 * Data source of an AlbumArtist.
 */
interface AlbumArtistDataSource {
    suspend fun getAll(): List<AlbumArtist>

    /**
     * Insert or updates an AlbumArtist.
     */
    suspend fun upsert(albumArtist: AlbumArtist)

    suspend fun upsertAll(albumArtists: List<AlbumArtist>)

    /**
     * Update the artist of an album.
     */
    suspend fun update(albumId: UUID, newArtistId: UUID)

    /**
     * Delete an album from an artist.
     */
    suspend fun delete(albumId: UUID)
}