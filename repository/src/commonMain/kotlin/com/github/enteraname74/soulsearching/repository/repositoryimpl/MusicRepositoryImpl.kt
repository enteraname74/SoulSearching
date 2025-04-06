package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.*
import com.github.enteraname74.domain.repository.CloudRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.enteraname74.domain.util.FlowResult
import com.github.enteraname74.domain.util.handleFlowResultOn
import com.github.enteraname74.soulsearching.features.filemanager.cloud.CloudCacheManager
import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverFileManager
import com.github.enteraname74.soulsearching.repository.datasource.CloudLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.DataModeDataSource
import com.github.enteraname74.soulsearching.repository.datasource.album.AlbumLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.artist.ArtistLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.music.MusicLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.music.MusicRemoteDataSource
import com.github.enteraname74.soulsearching.repository.datasource.musicartist.MusicArtistLocalDataSource
import com.github.enteraname74.soulsearching.repository.model.UploadedMusicResult
import com.github.enteraname74.soulsearching.repository.utils.DeleteAllHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
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
    private val playlistRepository: PlaylistRepository,
    private val cloudCacheManager: CloudCacheManager,
    private val coverFileManager: CoverFileManager,
) : MusicRepository, KoinComponent {
    private val cloudRepository: CloudRepository by inject()

    override val uploadFlow: MutableStateFlow<FlowResult<Unit>> = MutableStateFlow(
        FlowResult.Success(null)
    )

    override suspend fun upsert(
        music: Music,
        newCoverId: UUID?,
        artists: List<String>,
    ): SoulResult<Unit> =
        when(music.dataMode) {
            DataMode.Local -> {
                musicLocalDataSource.upsert(music = music)
                SoulResult.ofSuccess()
            }
            DataMode.Cloud -> {
                val result = musicRemoteDataSource.update(
                    music = music,
                    artists = artists,
                    newCover = newCoverId?.let {
                        coverFileManager.getPath(id = it)?.let(::File)
                    },
                )
                cloudRepository.syncDataWithCloud()
                result
            }
        }

    override suspend fun upsertAll(musics: List<Music>) {
        musicLocalDataSource.upsertAll(musics = musics)
    }

    override suspend fun delete(music: Music): SoulResult<Unit> =
        when (music.dataMode) {
            DataMode.Local -> {
                musicLocalDataSource.delete(music = music)
                SoulResult.ofSuccess()
            }
            DataMode.Cloud -> {
                val result = musicRemoteDataSource.deleteAll(
                    musicIds = listOf(music.musicId),
                )
                cloudRepository.syncDataWithCloud()
                result
            }
        }

    override suspend fun deleteAll(ids: List<UUID>): SoulResult<Unit> =
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
        dataMode: DataMode?,
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
        idsToDelete.forEach { musicIdToDelete ->
            cloudCacheManager.deleteFromId(id = musicIdToDelete)
        }

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
                        val musics: List<Music> = songsFromCloud.data
                        musicLocalDataSource.upsertAll(
                            musics = songsFromCloud.data,
                        )
                    }
                }
            }
        }
    }

    override suspend fun uploadAllMusicToCloud(
        onProgressChanged: suspend (Float) -> Unit,
    ): SoulResult<Unit> =
        handleFlowResultOn(flow = uploadFlow) { progressFunc ->
            val total = AtomicInteger(0)
            val allLocalSongs: List<Music> = musicLocalDataSource.getAll(DataMode.Local).first()

            val musicsToSave: ArrayList<Music> = arrayListOf()
            val localIdToRemote: HashMap<UUID, UUID> = hashMapOf()
            val albumsToSave: ArrayList<Album> = arrayListOf()
            val artistsToSave: ArrayList<Artist> = arrayListOf()
            val musicArtistsToSave: ArrayList<MusicArtist> = arrayListOf()

            allLocalSongs.forEach { music ->
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
                        val uploadedMusicResult: UploadedMusicResult = uploadResult.data
                        localIdToRemote[music.musicId] = uploadedMusicResult.music.musicId
                        musicsToSave.add(uploadedMusicResult.music)
                        albumsToSave.add(uploadedMusicResult.album)
                        artistsToSave.addAll(uploadedMusicResult.artists)
                        musicArtistsToSave.addAll(
                            uploadedMusicResult.artists.map { artist ->
                                MusicArtist(
                                    musicId = uploadedMusicResult.music.musicId,
                                    artistId = artist.artistId,
                                    dataMode = DataMode.Cloud,
                                )
                            }
                        )
                        total.getAndIncrement()
                        val progress = total.get().toFloat() / allLocalSongs.size
                        progressFunc(progress)
                        onProgressChanged(progress)
                    }
                }
            }

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

            playlistRepository.uploadAllToCloud(
                localMusicsToRemote = localIdToRemote,
            )
        }

    companion object {
        private const val MAX_SONGS_PER_PAGE = 50
    }
}