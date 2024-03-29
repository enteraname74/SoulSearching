package com.github.enteraname74.data.datasourceimpl

import com.github.enteraname74.data.AppDatabase
import com.github.enteraname74.data.model.toMusicPlaylist
import com.github.enteraname74.data.model.toRoomMusicPlaylist
import com.github.enteraname74.domain.datasource.MusicPlaylistDataSource
import com.github.enteraname74.domain.model.MusicPlaylist
import java.util.UUID
import javax.inject.Inject

/**
 * Implementation of the MusicPlaylistDataSource with Room's DAO.
 */
internal class MusicPlaylistDataSourceImpl @Inject constructor(
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