package com.github.enteraname74.soulsearching.localdesktop.datasourceimpl

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.soulsearching.localdesktop.dao.PlaylistDao
import com.github.enteraname74.soulsearching.repository.datasource.playlist.PlaylistLocalDataSource
import kotlinx.coroutines.flow.Flow
import java.util.*

internal class PlaylistLocalDataSourceImpl(
    private val playlistDao: PlaylistDao
) : PlaylistLocalDataSource {
    override suspend fun upsert(playlist: Playlist) {
        playlistDao.upsert(playlist)
    }

    override suspend fun upsertAll(playlists: List<Playlist>) {
        playlistDao.upsertAll(playlists)
    }

    override suspend fun delete(playlist: Playlist) {
        playlistDao.delete(playlist)
    }

    override suspend fun deleteAll(playlistIds: List<UUID>) {
        playlistDao.deleteAll(playlistIds)
    }

    override fun getAll(dataMode: DataMode): Flow<List<Playlist>> =
        playlistDao.getAll(dataMode.value)

    override fun getAllPlaylistWithMusics(dataMode: DataMode): Flow<List<PlaylistWithMusics>> =
        playlistDao.getAllPlaylistWithMusics(dataMode.value)

    override fun getFromId(playlistId: UUID): Flow<Playlist?> =
        playlistDao.getFromId(playlistId)

    override fun getPlaylistWithMusics(playlistId: UUID): Flow<PlaylistWithMusics?> =
        playlistDao.getPlaylistWithMusics(playlistId)

    override suspend fun deleteAll(dataMode: DataMode) {
        playlistDao.deleteAll(dataMode.value)
    }

    override suspend fun getAll(playlistIds: List<UUID>): List<Playlist> =
        playlistDao.getAll(playlistIds)
}