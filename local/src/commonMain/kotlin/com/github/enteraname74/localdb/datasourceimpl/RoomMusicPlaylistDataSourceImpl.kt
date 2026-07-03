package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.model.toMusicPlaylist
import com.github.enteraname74.localdb.model.toRoomMusicPlaylist
import com.github.enteraname74.soulsearching.repository.datasource.MusicPlaylistDataSource
import kotlin.uuid.Uuid

/**
 * Implementation of the MusicPlaylistDataSource with Room's DAO.
 */
internal class RoomMusicPlaylistDataSourceImpl(
    private val appDatabase: AppDatabase
) : MusicPlaylistDataSource {
    override suspend fun upsertMusicIntoPlaylist(musicPlaylist: MusicPlaylist) {
        appDatabase.musicPlaylistDao.upsertMusicIntoPlaylist(
            roomMusicPlaylist = musicPlaylist.toRoomMusicPlaylist()
        )
    }

    override suspend fun deleteMusicFromPlaylist(musicId: Uuid, playlistId: Uuid) {
        appDatabase.musicPlaylistDao.deleteMusicFromPlaylist(
            musicId = musicId,
            playlistId = playlistId
        )
    }

    override suspend fun getMusicPlaylist(musicId: Uuid, playlistId: Uuid): MusicPlaylist? {
        return appDatabase.musicPlaylistDao.getMusicPlaylist(
            musicId = musicId,
            playlistId = playlistId
        )?.toMusicPlaylist()
    }

    override suspend fun deleteMusicFromAllPlaylists(musicId: Uuid) {
        appDatabase.musicPlaylistDao.deleteMusicFromAllPlaylists(
            musicId = musicId
        )
    }
}
