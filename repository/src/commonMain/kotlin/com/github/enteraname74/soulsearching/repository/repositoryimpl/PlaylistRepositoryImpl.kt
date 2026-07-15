package com.github.enteraname74.soulsearching.repository.repositoryimpl

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.CloudPlaylist
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.PlaylistPreview
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverFileManager
import com.github.enteraname74.soulsearching.repository.datasource.playlist.PlaylistLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.playlist.PlaylistRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

/**
 * Repository of a Playlist.
 */
class PlaylistRepositoryImpl(
    private val playlistDataSource: PlaylistLocalDataSource,
    private val playlistRemoteDataSource: PlaylistRemoteDataSource,
    private val coverFileManager: CoverFileManager,
) : PlaylistRepository {
    override suspend fun upsert(playlist: Playlist) {
        playlistDataSource.upsert(
            playlist = playlist
        )
    }

    override suspend fun upsertAll(playlists: List<Playlist>) {
        playlistDataSource.upsertAll(playlists)
    }

    override suspend fun deleteAll(playlistIds: List<Uuid>): SoulResult<Unit> = SoulResult.runCatching {
        playlistDataSource.deleteAll(playlistIds)
        playlistRemoteDataSource.deleteAll(playlistIds)
    }

    override fun getAllPlaylistWithMusics(): Flow<List<PlaylistWithMusics>> =
        playlistDataSource.getAllPlaylistWithMusics()

    /**
     * Retrieves a Playlist from its id.
     */
    override fun getFromId(playlistId: Uuid): Flow<Playlist?> =
        playlistDataSource.getFromId(
            playlistId = playlistId
        )

    override fun getFromIds(playlistIds: List<Uuid>): Flow<List<PlaylistWithMusics>> =
        playlistDataSource.getFromIds(playlistIds)

    override suspend fun getFavorite(): Playlist? =
        playlistDataSource.getFavorite()

    override suspend fun getFromName(name: String): Playlist? =
        playlistDataSource.getFromName(name = name)

    /**
     * Retrieves a flow of a PlaylistWithMusics.
     */
    override fun getPlaylistWithMusics(playlistId: Uuid): Flow<PlaylistWithMusics?> =
        playlistDataSource.getPlaylistWithMusics(
            playlistId = playlistId
        )

    override fun getAllPaged(): Flow<PagingData<PlaylistPreview>> =
        playlistDataSource.getAllPaged()

    override suspend fun cleanAllCovers() {
        playlistDataSource.cleanAllCovers()
    }

    override fun getAllFromQuickAccess(): Flow<List<PlaylistPreview>> =
        playlistDataSource.getAllFromQuickAccess()

    override fun getMostListened(): Flow<List<PlaylistPreview>> =
        playlistDataSource.getMostListened()

    override fun getPlaylistPreview(playlistId: Uuid): Flow<PlaylistPreview?> =
        playlistDataSource.getPlaylistPreview(playlistId)

    override fun searchAll(search: String): Flow<List<PlaylistPreview>> =
        playlistDataSource.searchAll(search)

    override suspend fun getAllToSendToCloud(): List<PlaylistWithMusics> =
        playlistDataSource.getAllToSendToCloud()

    override suspend fun fetchUpdatedPlaylistsFromCloud(lastSyncMillis: Long?): List<CloudPlaylist> {
        var page = 0

        val fetchedPlaylists: MutableList<CloudPlaylist> = mutableListOf()
        while (true) {
            val fetchedData = playlistRemoteDataSource.getOfUser(
                lastUpdateAt = lastSyncMillis,
                maxPerPage = 1,
                page = page
            )

            fetchedPlaylists += fetchedData
            if (fetchedData.isEmpty()) break

            page += 1
        }

        return fetchedPlaylists
    }

    override suspend fun uploadToCloud(playlistWithMusics: PlaylistWithMusics): CloudPlaylist =
        playlistRemoteDataSource.upload(
            playlist = playlistWithMusics,
            coverPath = (playlistWithMusics.playlist.cover as? Cover.CoverFile)?.fileCoverId?.let { coverId ->
                coverFileManager.getCoverPath(coverId)
            },
        )
}
