package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.model.toRoomMusicPlaylist
import com.github.enteraname74.soulsearching.repository.datasource.musicplaylist.MusicPlaylistLocalDataSource

/**
 * Implementation of the MusicPlaylistDataSource with Room's DAO.
 */
internal class RoomMusicPlaylistLocalDataSourceImpl(
    private val appDatabase: AppDatabase
) : MusicPlaylistLocalDataSource {
    override suspend fun upsert(musicPlaylist: MusicPlaylist) {
        appDatabase.musicPlaylistDao.upsertMusicIntoPlaylist(
            roomMusicPlaylist = musicPlaylist.toRoomMusicPlaylist()
        )
    }

    override suspend fun delete(musicPlaylist: MusicPlaylist) {
        appDatabase.musicPlaylistDao.delete(musicPlaylist.toRoomMusicPlaylist())
    }

    override suspend fun upsertAll(musicPlaylists: List<MusicPlaylist>) {
        appDatabase.musicPlaylistDao.upsertAll(musicPlaylists.map { it.toRoomMusicPlaylist() })
    }

    override suspend fun deleteAll(musicPlaylists: List<MusicPlaylist>) {
        appDatabase.musicPlaylistDao.deleteAll(musicPlaylists.map { it.toRoomMusicPlaylist() })
    }

    override suspend fun deleteAll(dataMode: DataMode) {
        appDatabase.musicPlaylistDao.deleteAll(dataMode.value)
    }
}