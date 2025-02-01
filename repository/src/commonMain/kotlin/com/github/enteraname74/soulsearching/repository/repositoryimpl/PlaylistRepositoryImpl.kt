package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.*
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.enteraname74.soulsearching.repository.datasource.CloudLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.DataModeDataSource
import com.github.enteraname74.soulsearching.repository.datasource.musicplaylist.MusicPlaylistLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.playlist.PlaylistLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.playlist.PlaylistRemoteDataSource
import com.github.enteraname74.soulsearching.repository.model.UploadedPlaylistResult
import com.github.enteraname74.soulsearching.repository.utils.DeleteAllHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.util.*

/**
 * Repository of a Playlist.
 */
class PlaylistRepositoryImpl(
    private val playlistLocalDataSource: PlaylistLocalDataSource,
    private val playlistRemoteDataSource: PlaylistRemoteDataSource,
    private val musicPlaylistLocalDataSource: MusicPlaylistLocalDataSource,
    private val cloudLocalDataSource: CloudLocalDataSource,
    private val dataModeDataSource: DataModeDataSource,
) : PlaylistRepository {
    override suspend fun create(playlist: Playlist): SoulResult<Unit> =
        when (playlist.dataMode) {
            DataMode.Local -> {
                playlistLocalDataSource.upsert(playlist = playlist)
                SoulResult.ofSuccess()
            }

            DataMode.Cloud -> {
                val result: SoulResult<Playlist> = playlistRemoteDataSource.create(
                    playlistName = playlist.name
                )
                (result as? SoulResult.Success)?.data?.let {
                    playlistLocalDataSource.upsert(it)
                }

                result.toSimpleResult()
            }
        }


    override suspend fun update(playlist: Playlist): SoulResult<Unit> =
        when (playlist.dataMode) {
            DataMode.Local -> {
                playlistLocalDataSource.upsert(playlist = playlist)
                SoulResult.ofSuccess()
            }

            DataMode.Cloud -> {
                val result: SoulResult<Playlist> = playlistRemoteDataSource.update(
                    playlist = playlist,
                )
                (result as? SoulResult.Success)?.data?.let {
                    playlistLocalDataSource.upsert(it)
                }

                result.toSimpleResult()
            }
        }

    override suspend fun upsertAll(playlists: List<Playlist>) {
        playlistLocalDataSource.upsertAll(playlists)
    }

    override suspend fun delete(playlist: Playlist): SoulResult<Unit> =
        when (playlist.dataMode) {
            DataMode.Local -> {
                playlistLocalDataSource.delete(playlist = playlist)
                SoulResult.ofSuccess()
            }

            DataMode.Cloud -> {
                playlistRemoteDataSource.deleteAll(
                    playlistIds = listOf(playlist.playlistId),
                ).toSimpleResult()
            }
        }

    override suspend fun deleteAll(playlistIds: List<UUID>): SoulResult<Unit> =
        DeleteAllHelper.deleteAll(
            ids = playlistIds,
            getAll = playlistLocalDataSource::getAll,
            deleteAllLocal = playlistLocalDataSource::deleteAll,
            deleteAllRemote = playlistRemoteDataSource::deleteAll,
            mapIds = { it.playlistId },
            getDataMode = { it.dataMode },
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAll(
        dataMode: DataMode?,
    ): Flow<List<Playlist>> =
        dataModeDataSource
            .getCurrentDataModeWithUserCheck()
            .flatMapLatest { currentDataMode ->
                playlistLocalDataSource.getAll(
                    dataMode = dataMode ?: currentDataMode
                )
            }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAllPlaylistWithMusics(
        dataMode: DataMode?,
    ): Flow<List<PlaylistWithMusics>> =
        dataModeDataSource
            .getCurrentDataModeWithUserCheck()
            .flatMapLatest { currentDataMode ->
                playlistLocalDataSource.getAllPlaylistWithMusics(
                    dataMode = dataMode ?: currentDataMode
                )
            }

    /**
     * Retrieves a Playlist from its id.
     */
    override fun getFromId(playlistId: UUID): Flow<Playlist?> =
        playlistLocalDataSource.getFromId(
            playlistId = playlistId
        )

    /**
     * Retrieves a flow of a PlaylistWithMusics.
     */
    override fun getPlaylistWithMusics(playlistId: UUID): Flow<PlaylistWithMusics?> =
        playlistLocalDataSource.getPlaylistWithMusics(
            playlistId = playlistId
        )

    override suspend fun deleteAll(dataMode: DataMode) {
        playlistLocalDataSource.deleteAll(dataMode)
    }

    override suspend fun uploadAllToCloud(
        localMusicsToRemote: Map<UUID, UUID>,
    ): SoulResult<Unit> {
        val allLocalPlaylists: List<Playlist> = playlistLocalDataSource
            .getAll(DataMode.Local).first()

        val uploadedPlaylistsResult: SoulResult<List<UploadedPlaylistResult>> = playlistRemoteDataSource.upload(
            playlists = allLocalPlaylists,
        )

        return when (uploadedPlaylistsResult) {
            is SoulResult.Error -> {
                uploadedPlaylistsResult.toSimpleResult()
            }

            is SoulResult.Success -> {
                val playlistResultData: List<UploadedPlaylistResult> = uploadedPlaylistsResult.data
                playlistLocalDataSource.upsertAll(
                    playlists = playlistResultData.map { it.remotePlaylist },
                )
                linkMusicPlaylistToCloud(
                    uploadedPlaylistResult = playlistResultData,
                    localMusicsToRemote = localMusicsToRemote,
                )
            }
        }
    }

    private suspend fun linkMusicPlaylistToCloud(
        uploadedPlaylistResult: List<UploadedPlaylistResult>,
        localMusicsToRemote: Map<UUID, UUID>,
    ): SoulResult<Unit> {
        val allLocalPlaylistWithMusics: List<PlaylistWithMusics> = playlistLocalDataSource
            .getAllPlaylistWithMusics(DataMode.Local).first()

        val uploadedPlaylistLocalIds = uploadedPlaylistResult.map { it.localPlaylistId }

        val syncedPlaylistWithMusics: List<PlaylistWithMusics> = allLocalPlaylistWithMusics.filter {
            it.playlist.playlistId in uploadedPlaylistLocalIds
        }

        syncedPlaylistWithMusics.forEach { playlistWithMusics ->
            val remoteSongsIds: List<UUID> = playlistWithMusics.musics.mapNotNull {
                localMusicsToRemote[it.musicId]
            }
            val result: SoulResult<List<MusicPlaylist>> = playlistRemoteDataSource.addMusicsToPlaylist(
                playlistId = uploadedPlaylistResult
                    .first { it.localPlaylistId == playlistWithMusics.playlist.playlistId }.remotePlaylist.playlistId,
                musicIds = remoteSongsIds,
            )

            when(result) {
                is SoulResult.Error -> {
                    return result.toSimpleResult()
                }
                is SoulResult.Success -> {
                    musicPlaylistLocalDataSource.upsertAll(
                        musicPlaylists = result.data,
                    )
                }
            }
        }

        return SoulResult.ofSuccess()
    }

    override suspend fun syncWithCloud(): SoulResult<Unit> {
        var currentPage = 0
        val lastUpdateDate: LocalDateTime? = cloudLocalDataSource.getLastUpdateDate()

        val idsToDeleteResult: SoulResult<List<UUID>> = playlistRemoteDataSource.checkForDeletedPlaylists(
            playlistIds = playlistLocalDataSource.getAll(dataMode = DataMode.Cloud).first().map { it.playlistId }
        )

        val idsToDelete: List<UUID> = (idsToDeleteResult as? SoulResult.Success<List<UUID>>)?.data ?: emptyList()

        playlistLocalDataSource.deleteAll(idsToDelete)

        while (true) {
            val playlistFromCloud: SoulResult<List<Playlist>> = playlistRemoteDataSource.fetchPlaylistsFromCloud(
                after = lastUpdateDate,
                maxPerPage = MAX_PLAYLISTS_PER_PAGE,
                page = currentPage,
            )

            println("playlistRepositoryImpl -- syncWithCloud -- got result: $playlistFromCloud")

            when (playlistFromCloud) {
                is SoulResult.Error -> {
                    return SoulResult.Error(playlistFromCloud.error)
                }

                is SoulResult.Success -> {
                    if (playlistFromCloud.data.isEmpty()) {
                        return SoulResult.ofSuccess()
                    } else {
                        currentPage += 1
                        playlistLocalDataSource.upsertAll(
                            playlists = playlistFromCloud.data,
                        )
                    }
                }
            }
        }
    }

    companion object {
        private const val MAX_PLAYLISTS_PER_PAGE = 50
    }
}