package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.util.DateUtils
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
        appDatabase.playlistDao.updateLastUpdatedAtField(
            playlistIds = listOf(musicPlaylist.playlistId),
            updatedAt = DateUtils.now(),
        )
    }

    override suspend fun upsertAll(musicPlaylists: List<MusicPlaylist>) {
        appDatabase.musicPlaylistDao.upsertAll(
            musicPlaylists = musicPlaylists.map { it.toRoomMusicPlaylist() }
        )
        appDatabase.playlistDao.updateLastUpdatedAtField(
            playlistIds = musicPlaylists.map { it.playlistId },
            updatedAt = DateUtils.now(),
        )
    }

    override suspend fun deleteMusicFromPlaylist(musicId: Uuid, playlistId: Uuid) {
        appDatabase.musicPlaylistDao.deleteMusicFromPlaylist(
            musicId = musicId,
            playlistId = playlistId
        )
        appDatabase.playlistDao.updateLastUpdatedAtField(
            playlistIds = listOf(playlistId),
            updatedAt = DateUtils.now(),
        )
    }

    override suspend fun getMusicPlaylist(musicId: Uuid, playlistId: Uuid): MusicPlaylist? {
        return appDatabase.musicPlaylistDao.getMusicPlaylist(
            musicId = musicId,
            playlistId = playlistId
        )?.toMusicPlaylist()
    }

    override suspend fun deleteMusicFromAllPlaylists(musicId: Uuid) {
        val playlistIds = appDatabase.musicPlaylistDao.getPlaylistIdsOfMusic(musicId)
        appDatabase.musicPlaylistDao.deleteMusicFromAllPlaylists(
            musicId = musicId
        )
        appDatabase.playlistDao.updateLastUpdatedAtField(
            playlistIds = playlistIds,
            updatedAt = DateUtils.now(),
        )
    }
}
