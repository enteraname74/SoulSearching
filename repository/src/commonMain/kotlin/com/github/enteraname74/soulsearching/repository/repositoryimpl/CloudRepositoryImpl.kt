package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.*
import com.github.enteraname74.soulsearching.repository.datasource.CloudLocalDataSource
import kotlinx.coroutines.flow.Flow
import java.util.*

class CloudRepositoryImpl(
    private val musicRepository: MusicRepository,
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val musicArtistRepository: MusicArtistRepository,
    private val cloudLocalDataSource: CloudLocalDataSource,
): CloudRepository {
    override suspend fun clearLastUpdateDate() {
        cloudLocalDataSource.clearLastUpdateDate()
    }

    override suspend fun setSearchMetadata(searchMetadata: Boolean) {
        cloudLocalDataSource.setSearchMetadata(searchMetadata)
    }

    override fun getSearchMetadata(): Flow<Boolean> =
        cloudLocalDataSource.getSearchMetadata()

    override suspend fun syncDataWithCloud(): SoulResult<List<UUID>> {
        val artistSync: SoulResult<Unit> = artistRepository.syncWithCloud()
        if (artistSync.isError()) return artistSync.map { emptyList() }

        val albumSync: SoulResult<Unit> = albumRepository.syncWithCloud()
        if (albumSync.isError()) return albumSync.map { emptyList() }

        val musicSync: SoulResult<List<UUID>> = musicRepository.syncWithCloud()
        if (musicSync.isError()) return musicSync.map { emptyList() }

        val musicArtistSync: SoulResult<Unit> = musicArtistRepository.syncWithCloud()
        if (musicArtistSync.isError()) return musicArtistSync.map { emptyList() }

        cloudLocalDataSource.updateLastUpdateDate()

        return musicSync
    }
}