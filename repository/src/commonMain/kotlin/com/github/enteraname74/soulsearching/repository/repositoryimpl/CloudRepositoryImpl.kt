package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.*
import com.github.enteraname74.soulsearching.features.filemanager.cloud.CloudCacheManager
import com.github.enteraname74.soulsearching.repository.datasource.CloudLocalDataSource
import kotlinx.coroutines.flow.Flow
import java.util.*

class CloudRepositoryImpl(
    private val musicRepository: MusicRepository,
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val playlistRepository: PlaylistRepository,
    private val musicArtistRepository: MusicArtistRepository,
    private val musicPlaylistRepository: MusicPlaylistRepository,
    private val cloudLocalDataSource: CloudLocalDataSource,
    private val cloudCacheManager: CloudCacheManager,
): CloudRepository {
    override suspend fun clearLastUpdateDate() {
        cloudLocalDataSource.clearLastUpdateDate()
    }

    override suspend fun setSearchMetadata(searchMetadata: Boolean) {
        cloudLocalDataSource.setSearchMetadata(searchMetadata)
    }

    override suspend fun getAccessToken(): String =
        cloudLocalDataSource.getAccessToken()

    override fun getSearchMetadata(): Flow<Boolean> =
        cloudLocalDataSource.getSearchMetadata()

    override suspend fun deleteCloudCache() {
        cloudCacheManager.deleteAll()
    }

    override suspend fun syncDataWithCloud(): SoulResult<List<UUID>> {
        val artistSync: SoulResult<Unit> = artistRepository.syncWithCloud()
        if (artistSync.isError()) return artistSync.mapSuccess { emptyList() }

        val albumSync: SoulResult<Unit> = albumRepository.syncWithCloud()
        if (albumSync.isError()) return albumSync.mapSuccess { emptyList() }

        val musicSync: SoulResult<List<UUID>> = musicRepository.syncWithCloud()
        if (musicSync.isError()) return musicSync.mapSuccess { emptyList() }

        val musicArtistSync: SoulResult<Unit> = musicArtistRepository.syncWithCloud()
        if (musicArtistSync.isError()) return musicArtistSync.mapSuccess { emptyList() }

        val playlistSync: SoulResult<Unit> = playlistRepository.syncWithCloud()
        if (playlistSync.isError()) return playlistSync.mapSuccess { emptyList() }

        val musicPlaylistSync: SoulResult<Unit> = musicPlaylistRepository.syncWithCloud()
        if (musicPlaylistSync.isError()) return musicPlaylistSync.mapSuccess { emptyList() }

        cloudLocalDataSource.updateLastUpdateDate()

        return musicSync
    }
}