package com.github.enteraname74.localdesktop.datasourceimpl

import com.github.enteraname74.domain.datasource.MusicPlaylistDataSource
import com.github.enteraname74.domain.model.MusicPlaylist
import java.util.UUID

class ExposedMusicPlaylistDataSourceImpl: MusicPlaylistDataSource {
    override suspend fun insertMusicIntoPlaylist(musicPlaylist: MusicPlaylist) {

    }

    override suspend fun deleteMusicFromPlaylist(musicId: UUID, playlistId: UUID) {

    }

    override suspend fun getMusicPlaylist(musicId: UUID, playlistId: UUID): MusicPlaylist? {
        return null
    }

    override suspend fun deleteMusicFromAllPlaylists(musicId: UUID) {
    }
}