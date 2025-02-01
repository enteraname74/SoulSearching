package com.github.enteraname74.soulsearching.localdesktop.datasourceimpl

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.soulsearching.localdesktop.dao.MusicPlaylistDao
import com.github.enteraname74.soulsearching.repository.datasource.musicplaylist.MusicPlaylistLocalDataSource
import java.util.*

internal class MusicPlaylistLocalDataSourceImpl(
    private val musicPlaylistDao: MusicPlaylistDao
): MusicPlaylistLocalDataSource {
    override suspend fun upsert(musicPlaylist: MusicPlaylist) =
        musicPlaylistDao.upsert(musicPlaylist = musicPlaylist)

    override suspend fun upsertAll(musicPlaylists: List<MusicPlaylist>) {
        musicPlaylistDao.upsertAll(musicPlaylists)
    }

    override suspend fun delete(musicPlaylist: MusicPlaylist) {
        musicPlaylistDao.delete(musicPlaylist)
    }

    override suspend fun deleteAll(musicPlaylists: List<MusicPlaylist>) {
       musicPlaylistDao.deleteAll(
           musicPlaylistsIds = musicPlaylists.map { it.id }
       )
    }

    override suspend fun deleteAll(dataMode: DataMode) {
        musicPlaylistDao.deleteAll(dataMode.value)
    }
}