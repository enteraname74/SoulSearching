package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.model.toPlaylist
import com.github.enteraname74.localdb.model.toPlaylistWIthMusics
import com.github.enteraname74.localdb.model.toRoomPlaylist
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.soulsearching.repository.datasource.PlaylistDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

internal class RoomPlaylistDataSourceImpl(
    private val appDatabase: AppDatabase
) : PlaylistDataSource {
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

    override fun getAll(): Flow<List<Playlist>> {
        return appDatabase.playlistDao.getAll().map { list ->
            list.map { it.toPlaylist() }
        }
    }

    override fun getAllPlaylistWithMusics(): Flow<List<PlaylistWithMusics>> {
        return appDatabase.playlistDao.getAllPlaylistWithMusics().map { list ->
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
}