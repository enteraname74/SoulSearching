package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.model.toPlaylist
import com.github.enteraname74.localdb.model.toPlaylistWIthMusics
import com.github.enteraname74.localdb.model.toRoomPlaylist
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.soulsearching.repository.datasource.playlist.PlaylistLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

internal class RoomPlaylistLocalDataSourceImpl(
    private val appDatabase: AppDatabase
) : PlaylistLocalDataSource {
    override suspend fun upsert(playlist: Playlist) {
        appDatabase.playlistDao.upsert(
            roomPlaylist = playlist.toRoomPlaylist()
        )
    }

    override suspend fun upsertAll(playlists: List<Playlist>) {
        appDatabase.playlistDao.upsertAll(
            roomPlaylists = playlists.map { it.toRoomPlaylist() }
        )
    }

    override suspend fun delete(playlist: Playlist) {
        appDatabase.playlistDao.delete(
            roomPlaylist = playlist.toRoomPlaylist()
        )
    }

    override suspend fun deleteAll(playlistIds: List<UUID>) {
        appDatabase.playlistDao.deleteAll(
            ids = playlistIds,
        )
    }

    override fun getAll(dataMode: DataMode): Flow<List<Playlist>> {
        return appDatabase.playlistDao.getAll(dataMode.value).map { list ->
            list.map { it.toPlaylist() }
        }
    }

    override fun getAllPlaylistWithMusics(dataMode: DataMode): Flow<List<PlaylistWithMusics>> {
        return appDatabase.playlistDao.getAllPlaylistWithMusics(dataMode.value).map { list ->
            list.map { it.toPlaylistWIthMusics() }
        }
    }

    override fun getFromId(playlistId: UUID): Flow<Playlist?> {
        return appDatabase.playlistDao.getFromId(
            playlistId = playlistId
        ).map { it?.toPlaylist() }
    }

    override fun getPlaylistWithMusics(playlistId: UUID): Flow<PlaylistWithMusics?> {
        return appDatabase.playlistDao.getPlaylistWithMusics(playlistId = playlistId)
            .map { it?.toPlaylistWIthMusics() }
    }

    override suspend fun deleteAll(dataMode: DataMode) {
        appDatabase.playlistDao.deleteAll(dataMode.value)
    }

    override suspend fun getAll(playlistIds: List<UUID>): List<Playlist> =
        appDatabase.playlistDao.getAll(playlistIds).map { it.toPlaylist() }
}