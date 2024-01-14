package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.domain.datasource.MusicPlaylistDataSource
import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.model.toMusicPlaylist
import com.github.enteraname74.localdb.model.toRoomMusicPlaylist
import com.github.enteraname74.domain.model.MusicPlaylist
import java.util.UUID

/**
 * Implementation of the MusicPlaylistDataSource with Room's DAO.
 */
internal class RoomMusicPlaylistDataSourceImpl(
    private val appDatabase: AppDatabase
) : MusicPlaylistDataSource {
    override suspend fun insertMusicIntoPlaylist(musicPlaylist: MusicPlaylist) {
        appDatabase.musicPlaylistDao.insertMusicIntoPlaylist(
            roomMusicPlaylist = musicPlaylist.toRoomMusicPlaylist()
        )
    }

    override suspend fun deleteMusicFromPlaylist(musicId: UUID, playlistId: UUID) {
        appDatabase.musicPlaylistDao.deleteMusicFromPlaylist(
            musicId = musicId,
            playlistId = playlistId
        )
    }

    override suspend fun getMusicPlaylist(musicId: UUID, playlistId: UUID): MusicPlaylist? {
        return appDatabase.musicPlaylistDao.getMusicPlaylist(
            musicId = musicId,
            playlistId = playlistId
        )?.toMusicPlaylist()
    }

    override suspend fun deleteMusicFromAllPlaylists(musicId: UUID) {
        appDatabase.musicPlaylistDao.deleteMusicFromAllPlaylists(
            musicId = musicId
        )
    }
}