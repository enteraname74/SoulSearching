package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.*
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.util.FlowResult
import com.github.enteraname74.domain.util.handleFlowResultOn
import com.github.enteraname74.soulsearching.repository.datasource.CloudLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.DataModeDataSource
import com.github.enteraname74.soulsearching.repository.datasource.album.AlbumLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.artist.ArtistLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.music.MusicLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.music.MusicRemoteDataSource
import com.github.enteraname74.soulsearching.repository.datasource.musicartist.MusicArtistLocalDataSource
import com.github.enteraname74.soulsearching.repository.model.UploadedMusicResult
import com.github.enteraname74.soulsearching.repository.utils.DeleteAllHelper
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
    private val musicArtistLocalDataSource: MusicArtistLocalDataSource,
    private val albumLocalDataSource: AlbumLocalDataSource,
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

    override suspend fun delete(music: Music): SoulResult<String> =
        when (music.dataMode) {
            DataMode.Local -> {
                musicLocalDataSource.delete(music = music)
                SoulResult.Success("")
            }
            DataMode.Cloud -> {
                musicRemoteDataSource.deleteAll(
                    musicIds = listOf(music.musicId),
                )
            }
        }

    override suspend fun deleteAll(ids: List<UUID>): SoulResult<String> =
        DeleteAllHelper.deleteAll(
            ids = ids,
            getAll = musicLocalDataSource::getAll,
            deleteAllLocal = musicLocalDataSource::deleteAll,
            deleteAllRemote = musicRemoteDataSource::deleteAll,
            mapIds = { it.musicId },
            getDataMode = { it.dataMode },
        )

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
    override fun getAll(
        dataMode: DataMode?
    ): Flow<List<Music>> =
        dataModeDataSource
            .getCurrentDataModeWithUserCheck()
            .flatMapLatest { currentDataMode ->
                musicLocalDataSource.getAll(
                    dataMode = dataMode ?: currentDataMode,
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

            val musicsToSave: ArrayList<Music> = arrayListOf()
            val albumsToSave: ArrayList<Album> = arrayListOf()
            val artistsToSave: ArrayList<Artist> = arrayListOf()
            val musicArtistsToSave: ArrayList<MusicArtist> = arrayListOf()

            allLocalSongs.forEach { music ->
                val job = CoroutineScope(Dispatchers.IO).launch {
                    val uploadResult: SoulResult<UploadedMusicResult> = musicRemoteDataSource.uploadMusicToCloud(
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
                            (uploadResult.data as? UploadedMusicResult.Data)?.let { data ->
                                total.getAndIncrement()
                                progressFunc(
                                    total.get().toFloat() / allLocalSongs.size
                                )

                                musicsToSave.add(data.music)
                                albumsToSave.add(data.album)
                                artistsToSave.addAll(data.artists)
                                musicArtistsToSave.addAll(
                                    data.artists.map { artist ->
                                        MusicArtist(
                                            musicId = data.music.musicId,
                                            artistId = artist.artistId,
                                            dataMode = DataMode.Cloud,
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
                jobs.add(job)
            }

            jobs.forEach { it.join() }

            artistLocalDataSource.upsertAll(
                artists = artistsToSave,
            )
            albumLocalDataSource.upsertAll(
                albums = albumsToSave,
            )
            musicLocalDataSource.upsertAll(
                musics = musicsToSave,
            )
            musicArtistLocalDataSource.upsertAll(
                musicArtists = musicArtistsToSave
            )

            SoulResult.Success(Unit)
        }

    companion object {
        private const val MAX_SONGS_PER_PAGE = 50
    }
}