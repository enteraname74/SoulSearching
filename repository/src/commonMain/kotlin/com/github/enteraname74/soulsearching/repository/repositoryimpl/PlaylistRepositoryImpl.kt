package com.github.enteraname74.soulsearching.repository.repositoryimpl

import androidx.paging.PagingData
import com.github.enteraname74.soulsearching.domain.model.CloudPlaylist
import com.github.enteraname74.soulsearching.domain.model.Cover
import com.github.enteraname74.soulsearching.domain.model.Playlist
import com.github.enteraname74.soulsearching.domain.model.PlaylistPreview
import com.github.enteraname74.soulsearching.domain.model.PlaylistWithMusics
import com.github.enteraname74.soulsearching.domain.model.SoulResult
import com.github.enteraname74.soulsearching.domain.repository.PlaylistRepository
import com.github.enteraname74.soulsearching.domain.repository.CoverRepository
import com.github.enteraname74.soulsearching.repository.datasource.playlist.PlaylistLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.playlist.PlaylistRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

/**
 * Repository of a Playlist.
 */
class PlaylistRepositoryImpl(
    private val playlistLocalDataSource: PlaylistLocalDataSource,
    private val playlistRemoteDataSource: PlaylistRemoteDataSource,
    private val coverRepository: CoverRepository,
) : PlaylistRepository {
    override suspend fun upsert(playlist: Playlist) {
        playlistLocalDataSource.upsert(
            playlist = playlist
        )
    }

    override suspend fun upsertAll(playlists: List<Playlist>, keepUpdatedAt: Boolean) {
        playlistLocalDataSource.upsertAll(playlists, keepUpdatedAt)
    }

    override suspend fun deleteAll(playlistIds: List<Uuid>): SoulResult<Unit> = SoulResult.runCatching {
        val remoteIds: List<Uuid> = playlistLocalDataSource.getRemoteIdsFromIds(ids = playlistIds)
        playlistLocalDataSource.deleteAll(playlistIds)
        playlistRemoteDataSource.deleteAll(remoteIds)
    }

    override suspend fun deleteAllFromRemote(remoteIds: List<Uuid>) {
        playlistLocalDataSource.deleteAllFromRemote(remoteIds = remoteIds)
    }

    override fun getAllPlaylistWithMusics(): Flow<List<PlaylistWithMusics>> =
        playlistLocalDataSource.getAllPlaylistWithMusics()

    /**
     * Retrieves a Playlist from its id.
     */
    override fun getFromId(playlistId: Uuid): Flow<Playlist?> =
        playlistLocalDataSource.getFromId(
            playlistId = playlistId
        )

    override suspend fun getFromRemoteId(remoteId: Uuid): Playlist? =
        playlistLocalDataSource.getFromRemoteId(remoteId = remoteId)

    override fun getFromIds(playlistIds: List<Uuid>): Flow<List<PlaylistWithMusics>> =
        playlistLocalDataSource.getFromIds(playlistIds)

    override suspend fun getFavorite(): Playlist? =
        playlistLocalDataSource.getFavorite()

    override fun observeFavorite(): Flow<PlaylistWithMusics?> =
        playlistLocalDataSource.observeFavorite()

    override suspend fun getFromName(name: String): Playlist? =
        playlistLocalDataSource.getFromName(name = name)

    /**
     * Retrieves a flow of a PlaylistWithMusics.
     */
    override fun getPlaylistWithMusics(playlistId: Uuid): Flow<PlaylistWithMusics?> =
        playlistLocalDataSource.getPlaylistWithMusics(
            playlistId = playlistId
        )

    override fun getAllPaged(): Flow<PagingData<PlaylistPreview>> =
        playlistLocalDataSource.getAllPaged()

    override suspend fun getAll(page: Int, pageSize: Int): List<PlaylistPreview> =
        playlistLocalDataSource.getAll(page = page, pageSize = pageSize)

    override suspend fun cleanAllCovers() {
        playlistLocalDataSource.cleanAllCovers()
    }

    override fun getAllFromQuickAccess(): Flow<List<PlaylistPreview>> =
        playlistLocalDataSource.getAllFromQuickAccess()

    override fun getPlaylistPreview(playlistId: Uuid): Flow<PlaylistPreview?> =
        playlistLocalDataSource.getPlaylistPreview(playlistId)

    override fun searchAll(search: String): Flow<List<PlaylistPreview>> =
        playlistLocalDataSource.searchAll(search)

    override suspend fun getAllToSendToCloud(): List<PlaylistWithMusics> =
        playlistLocalDataSource.getAllToSendToCloud()

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
            coversData = playlistWithMusics.playlist.cover.toUploadData(),
        )

    private suspend fun Cover?.toUploadData(): List<ByteArray?> = when (this) {
        null -> emptyList()
        is Cover.CoverFile -> listOf(coverRepository.getCoverData(this))
        is Cover.Url -> listOf(null)
        is Cover.Grid -> list.map { (it as? Cover.CoverFile)?.let { coverFile -> coverRepository.getCoverData(coverFile) } }
    }

    override suspend fun getDeletedRemotePlaylistIds(): List<Uuid> {
        val allRemoteIds: List<Uuid> = playlistLocalDataSource.getAllRemoteIdsPossessedByUser()
        return playlistRemoteDataSource.getDeletedRemotePlaylistIds(allRemoteIds)
    }

    override suspend fun deleteAllRemoteFields() {
        playlistLocalDataSource.deleteAllRemoteFields()
    }

    override suspend fun deleteAllEmptyExceptFavorite() {
        playlistLocalDataSource.deleteAllEmptyExceptFavorite()
    }

    override suspend fun getLatestUpdatedAt(): Long? =
        playlistLocalDataSource.getLatestUpdatedAt()

    override fun getTotal(): Flow<Int> =
        playlistLocalDataSource.getTotal()
}
