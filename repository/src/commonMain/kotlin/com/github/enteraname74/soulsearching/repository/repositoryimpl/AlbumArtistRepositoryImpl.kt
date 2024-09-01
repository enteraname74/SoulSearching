package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.AlbumArtist
import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.soulsearching.repository.datasource.AlbumArtistDataSource
import java.util.*

class AlbumArtistRepositoryImpl(
    private val albumArtistDataSource: AlbumArtistDataSource
): AlbumArtistRepository {
    override suspend fun upsert(albumArtist: AlbumArtist) =
        albumArtistDataSource.upsert(albumArtist = albumArtist)

    override suspend fun update(albumId: UUID, newArtistId: UUID) {
        albumArtistDataSource.update(
            albumId = albumId,
            newArtistId = newArtistId,
        )
    }

    override suspend fun delete(albumId: UUID) {
        albumArtistDataSource.delete(
            albumId = albumId,
        )
    }
}