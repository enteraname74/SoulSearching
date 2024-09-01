package com.github.enteraname74.soulsearching.localdesktop.datasourceimpl

import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.soulsearching.localdesktop.dao.PlaylistDao
import com.github.enteraname74.soulsearching.repository.datasource.PlaylistDataSource
import kotlinx.coroutines.flow.Flow
import java.util.*

internal class PlaylistDataSourceImpl(
    private val playlistDao: PlaylistDao
) : PlaylistDataSource {
    override suspend fun upsert(playlist: Playlist) {
        playlistDao.upsert(playlist)
    }

    override suspend fun delete(playlist: Playlist) {
        playlistDao.delete(playlist)
    }

    override fun getAll(): Flow<List<Playlist>> =
        playlistDao.getAll()

    override fun getAllPlaylistWithMusics(): Flow<List<PlaylistWithMusics>> =
        playlistDao.getAllPlaylistWithMusics()

    override fun getFromId(playlistId: UUID): Flow<Playlist?> =
        playlistDao.getFromId(playlistId)

    override fun getPlaylistWithMusics(playlistId: UUID): Flow<PlaylistWithMusics?> =
        playlistDao.getPlaylistWithMusics(playlistId)
}