package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.soulsearching.repository.datasource.CloudLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.DataModeDataSource
import com.github.enteraname74.soulsearching.repository.datasource.music.MusicLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.music.MusicRemoteDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import java.time.LocalDateTime
import java.util.*

/**
 * Repository for handling Music related work.
 */
class MusicRepositoryImpl(
    private val musicLocalDataSource: MusicLocalDataSource,
    private val musicRemoteDataSource: MusicRemoteDataSource,
    private val dataModeDataSource: DataModeDataSource,
    private val cloudLocalDataSource: CloudLocalDataSource,
) : MusicRepository {
    override suspend fun upsert(music: Music) {
        musicLocalDataSource.upsert(music = music)
    }

    override suspend fun upsertAll(musics: List<Music>) {
        musicLocalDataSource.upsertAll(musics = musics)
    }

    override suspend fun delete(music: Music) {
        musicLocalDataSource.delete(music = music)
    }

    override suspend fun deleteAll(ids: List<UUID>) {
        musicLocalDataSource.deleteAll(ids = ids)
    }

    override suspend fun deleteAll(dataMode: DataMode) {
        musicLocalDataSource.deleteAll(dataMode)
    }

    override fun getFromId(musicId: UUID): Flow<Music?> = musicLocalDataSource.getFromId(
        musicId = musicId
    )

    override suspend fun getFromPath(musicPath: String): Music? =
        musicLocalDataSource.getFromPath(
            musicPath = musicPath,
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAll(): Flow<List<Music>> =
        dataModeDataSource
            .getCurrentDataModeWithUserCheck()
            .flatMapLatest { dataMode ->
                musicLocalDataSource.getAll(
                    dataMode = dataMode,
                )
            }

    override suspend fun getAllMusicFromAlbum(albumId: UUID): List<Music> =
        musicLocalDataSource.getAllMusicFromAlbum(
            albumId = albumId
        )

    override suspend fun syncWithCloud(): SoulResult<Unit> {
        var currentPage = 0
        val lastUpdateDate: LocalDateTime? = cloudLocalDataSource.getLastUpdateDate()

        while(true) {
            val songsFromCloud: SoulResult<List<Music>> = musicRemoteDataSource.fetchSongsFromCloud(
                after = lastUpdateDate,
                maxPerPage = MAX_SONGS_PER_PAGE,
                page = currentPage,
            )

            when (songsFromCloud) {
                is SoulResult.Error -> {
                    return songsFromCloud.toSimpleResult()
                }

                is SoulResult.Success -> {
                    if (songsFromCloud.result.isEmpty()) {
                        cloudLocalDataSource.updateLastUpdateDate()
                        return SoulResult.ofSuccess()
                    }
                    currentPage += 1
                    musicLocalDataSource.upsertAll(musics = songsFromCloud.result)
                }
            }
        }
    }

    companion object {
        private const val MAX_SONGS_PER_PAGE = 1
    }
}