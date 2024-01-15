package com.github.enteraname74.localdesktop.daoimpl

import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.localdesktop.dao.MusicPlaylistDao
import java.util.UUID

/**
 * Implementation of the MusicPlaylistDao for Exposed.
 */
class ExposedMusicPlaylistDaoImpl: MusicPlaylistDao {
    override suspend fun insertMusicIntoPlaylist(musicPlaylist: MusicPlaylist) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMusicFromPlaylist(musicId: UUID, playlistId: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun getMusicPlaylist(musicId: UUID, playlistId: UUID): MusicPlaylist? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMusicFromAllPlaylists(musicId: UUID) {
        TODO("Not yet implemented")
    }
}