package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.soulsearching.repository.datasource.musicplaylist.MusicPlaylistLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.musicplaylist.MusicPlaylistRemoteDataSource

/**
 * Repository of a MusicPlaylist.
 */
class MusicPlaylistRepositoryImpl(
    private val musicPlaylistLocalDataSource: MusicPlaylistLocalDataSource,
    private val musicPlaylistRemoteDataSource: MusicPlaylistRemoteDataSource,
) : MusicPlaylistRepository {
    override suspend fun upsert(musicPlaylist: MusicPlaylist) =
        musicPlaylistLocalDataSource.upsert(
            musicPlaylist = musicPlaylist
        )

    override suspend fun deleteAll(dataMode: DataMode) {
        musicPlaylistLocalDataSource.deleteAll(dataMode)
    }

    override suspend fun delete(musicPlaylist: MusicPlaylist) =
        musicPlaylistLocalDataSource.delete(musicPlaylist)

    override suspend fun upsertAll(musicPlaylists: List<MusicPlaylist>) {
        musicPlaylistLocalDataSource.upsertAll(musicPlaylists)
    }

    override suspend fun syncWithCloud(): SoulResult<Unit> {
        var currentPage = 0

        musicPlaylistLocalDataSource.deleteAll(DataMode.Cloud)

        while (true) {
            val musicPlaylistsFromCloud: SoulResult<List<MusicPlaylist>> =
                musicPlaylistRemoteDataSource.fetchMusicPlaylistsFromCloud(
                    after = null,
                    maxPerPage = MAX_MUSIC_PLAYLISTS_PER_PAGE,
                    page = currentPage,
                )

            println("musicPlaylistRepositoryImpl -- syncWithCloud -- got result: $musicPlaylistsFromCloud")

            when (musicPlaylistsFromCloud) {
                is SoulResult.Error -> {
                    return SoulResult.Error(musicPlaylistsFromCloud.error)
                }

                is SoulResult.Success -> {
                    if (musicPlaylistsFromCloud.data.isEmpty()) {
                        return SoulResult.ofSuccess()
                    } else {
                        currentPage += 1
                        musicPlaylistLocalDataSource.upsertAll(
                            musicPlaylists = musicPlaylistsFromCloud.data,
                        )
                    }
                }
            }
        }
    }

    companion object {
        private const val MAX_MUSIC_PLAYLISTS_PER_PAGE = 50
    }
}