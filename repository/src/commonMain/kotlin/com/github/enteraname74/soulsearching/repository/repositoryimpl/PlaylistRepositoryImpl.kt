package com.github.enteraname74.soulsearching.repository.repositoryimpl

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.PlaylistPreview
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.enteraname74.soulsearching.repository.datasource.PlaylistDataSource
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * Repository of a Playlist.
 */
class PlaylistRepositoryImpl(
    private val playlistDataSource: PlaylistDataSource
): PlaylistRepository {
    override suspend fun upsert(playlist: Playlist) = playlistDataSource.upsert(
        playlist = playlist
    )

    override suspend fun upsertAll(playlists: List<Playlist>) {
        playlistDataSource.upsertAll(playlists)
    }

    override suspend fun delete(playlist: Playlist) = playlistDataSource.delete(
        playlist = playlist
    )

    override suspend fun deleteAll(playlistIds: List<UUID>) {
        playlistDataSource.deleteAll(playlistIds)
    }

    override fun getAllPlaylistWithMusics(): Flow<List<PlaylistWithMusics>> =
        playlistDataSource.getAllPlaylistWithMusics()

    /**
     * Retrieves a Playlist from its id.
     */
    override fun getFromId(playlistId: UUID): Flow<Playlist?> =
        playlistDataSource.getFromId(
            playlistId = playlistId
        )

    /**
     * Retrieves a flow of a PlaylistWithMusics.
     */
    override fun getPlaylistWithMusics(playlistId: UUID): Flow<PlaylistWithMusics?> =
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

    override fun getPlaylistPreview(playlistId: UUID): Flow<PlaylistPreview?> =
        playlistDataSource.getPlaylistPreview(playlistId)
}