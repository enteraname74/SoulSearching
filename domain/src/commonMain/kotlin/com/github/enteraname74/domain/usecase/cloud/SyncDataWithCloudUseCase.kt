package com.github.enteraname74.domain.usecase.cloud

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.*
import java.util.UUID

class SyncDataWithCloudUseCase(
    private val musicRepository: MusicRepository,
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val musicArtistRepository: MusicArtistRepository,
    private val cloudRepository: CloudRepository,
) {

    /**
     * Sync artists, albums, songs and playlist with the cloud.
     * If all elements were fetched successfully, the last update at date is updated.
     */
    suspend operator fun invoke(): SoulResult<List<UUID>> {
        val artistSync: SoulResult<Unit> = artistRepository.syncWithCloud()
        if (artistSync.isError()) return artistSync.map { emptyList() }

        val albumSync: SoulResult<Unit> = albumRepository.syncWithCloud()
        if (albumSync.isError()) return albumSync.map { emptyList() }

        val musicSync: SoulResult<List<UUID>> = musicRepository.syncWithCloud()
        if (musicSync.isError()) return musicSync.map { emptyList() }

        val musicArtistSync: SoulResult<Unit> = musicArtistRepository.syncWithCloud()
        if (musicArtistSync.isError()) return musicArtistSync.map { emptyList() }

        cloudRepository.updateLastUpdateDate()

        return musicSync
    }

}