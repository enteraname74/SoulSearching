package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.util.FlowResult
import com.github.enteraname74.domain.util.handleFlowResultOn
import com.github.enteraname74.soulsearching.repository.datasource.CloudLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.DataModeDataSource
import com.github.enteraname74.soulsearching.repository.datasource.artist.ArtistLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.music.MusicLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.music.MusicRemoteDataSource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Repository for handling Music related work.
 */
class MusicRepositoryImpl(
    private val musicLocalDataSource: MusicLocalDataSource,
    private val musicRemoteDataSource: MusicRemoteDataSource,
    private val artistLocalDataSource: ArtistLocalDataSource,
    private val dataModeDataSource: DataModeDataSource,
    private val cloudLocalDataSource: CloudLocalDataSource,
) : MusicRepository {
    override val uploadFlow: MutableStateFlow<FlowResult<Unit>> = MutableStateFlow(
        FlowResult.Success(null)
    )

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

    override suspend fun updateMusicsAlbum(newAlbumId: UUID, legacyAlbumId: UUID) {
        musicLocalDataSource.updateMusicsAlbum(
            newAlbumId = newAlbumId,
            legacyAlbumId = legacyAlbumId,
        )
    }

    override suspend fun syncWithCloud(): SoulResult<List<UUID>> {
        var currentPage = 0
        val lastUpdateDate: LocalDateTime? = cloudLocalDataSource.getLastUpdateDate()

        val idsToDeleteResult: SoulResult<List<UUID>> = musicRemoteDataSource.checkForDeletedSongs(
            musicIds = musicLocalDataSource.getAll(dataMode = DataMode.Cloud).first().map { it.musicId }
        )

        val idsToDelete: List<UUID> = (idsToDeleteResult as? SoulResult.Success<List<UUID>>)?.data ?: emptyList()

        musicLocalDataSource.deleteAll(idsToDelete)

        while(true) {
            val songsFromCloud: SoulResult<List<Music>> = musicRemoteDataSource.fetchSongsFromCloud(
                after = lastUpdateDate,
                maxPerPage = MAX_SONGS_PER_PAGE,
                page = currentPage,
            )

            println("musicRepositoryImpl -- syncWithCloud -- got result: $songsFromCloud")

            when (songsFromCloud) {
                is SoulResult.Error -> {
                    return SoulResult.Error(songsFromCloud.error)
                }

                is SoulResult.Success -> {
                    if (songsFromCloud.data.isEmpty()) {
                        return SoulResult.Success(idsToDelete)
                    } else {
                        currentPage += 1
                        musicLocalDataSource.upsertAll(
                            musics = songsFromCloud.data,
                        )
                    }

                }
            }
        }
    }

    override suspend fun uploadAllMusicToCloud(): SoulResult<Unit> =
        handleFlowResultOn(flow = uploadFlow) { progressFunc ->
            val total = AtomicInteger(0)
            val allLocalSongs: List<Music> = musicLocalDataSource.getAll(DataMode.Local).first()

            val jobs = ArrayList<Job>()

            allLocalSongs.forEach { music ->
                val job = CoroutineScope(Dispatchers.IO).launch {
                    val uploadResult: SoulResult<Music?> = musicRemoteDataSource.uploadMusicToCloud(
                        music = music,
                        searchMetadata = cloudLocalDataSource.getSearchMetadata().first(),
                        artists = artistLocalDataSource.getArtistsOfMusic(
                            musicId = music.musicId,
                        ).first().map { it.artistName }
                    )

                    println("MusicRepositoryImpl -- uploadAllMusicToCloud -- got result for ${music.name}: $uploadResult")

                    when(uploadResult) {
                        is SoulResult.Error -> {
                            /*no-op*/
                        }
                        is SoulResult.Success -> {
                            uploadResult.data?.let {
                                total.getAndIncrement()
                                progressFunc(
                                    total.get().toFloat() / allLocalSongs.size
                                )
                                musicLocalDataSource.upsert(
                                    music = it,
                                )
                            }
                        }
                    }
                }
                jobs.add(job)
            }

            jobs.forEach { it.join() }
            SoulResult.Success(Unit)
        }

    companion object {
        private const val MAX_SONGS_PER_PAGE = 50
    }
}