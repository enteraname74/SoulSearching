package com.github.enteraname74.localdesktop.datasourceimpl

import com.github.enteraname74.domain.datasource.MusicPlaylistDataSource
import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.localdesktop.dao.MusicPlaylistDao
import java.util.UUID

/**
 * Implementation of the MusicPlaylistDataSource with Exposed.
 */
internal class ExposedMusicPlaylistDataSourceImpl(
    private val musicPlaylistDao: MusicPlaylistDao
): MusicPlaylistDataSource {
    override suspend fun insertMusicIntoPlaylist(musicPlaylist: MusicPlaylist) =
        musicPlaylistDao.insertMusicIntoPlaylist(musicPlaylist = musicPlaylist)

    override suspend fun deleteMusicFromPlaylist(musicId: UUID, playlistId: UUID) =
        musicPlaylistDao.deleteMusicFromPlaylist(
            musicId = musicId,
            playlistId = playlistId
        )

    override suspend fun getMusicPlaylist(musicId: UUID, playlistId: UUID) =
        musicPlaylistDao.getMusicPlaylist(
            musicId = musicId,
            playlistId = playlistId
        )

    override suspend fun deleteMusicFromAllPlaylists(musicId: UUID) =
        musicPlaylistDao.deleteMusicFromAllPlaylists(musicId = musicId)
}