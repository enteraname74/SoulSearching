package com.github.enteraname74.soulsearching.localdesktop.datasourceimpl

import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.soulsearching.localdesktop.dao.MusicPlaylistDao
import com.github.enteraname74.soulsearching.repository.datasource.MusicPlaylistDataSource
import java.util.UUID

internal class MusicPlaylistDataSourceImpl(
    private val musicPlaylistDao: MusicPlaylistDao
): MusicPlaylistDataSource {
    override suspend fun upsertMusicIntoPlaylist(musicPlaylist: MusicPlaylist) =
        musicPlaylistDao.upsertMusicIntoPlaylist(musicPlaylist = musicPlaylist)

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