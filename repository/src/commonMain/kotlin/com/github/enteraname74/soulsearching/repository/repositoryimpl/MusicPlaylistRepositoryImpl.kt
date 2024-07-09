package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.soulsearching.repository.datasource.MusicPlaylistDataSource
import java.util.*

/**
 * Repository of a MusicPlaylist.
 */
class MusicPlaylistRepositoryImpl(
    private val musicPlaylistDataSource: MusicPlaylistDataSource
): MusicPlaylistRepository {
    override suspend fun insertMusicIntoPlaylist(musicPlaylist: MusicPlaylist) =
        musicPlaylistDataSource.insertMusicIntoPlaylist(
            musicPlaylist = musicPlaylist
        )

    override suspend fun deleteMusicFromPlaylist(musicId: UUID, playlistId: UUID) =
        musicPlaylistDataSource.deleteMusicFromPlaylist(
            musicId = musicId,
            playlistId = playlistId
        )

    override suspend fun getMusicPlaylist(musicId: UUID, playlistId: UUID): MusicPlaylist? =
        musicPlaylistDataSource.getMusicPlaylist(
            musicId = musicId,
            playlistId = playlistId
        )

    override suspend fun deleteMusicFromAllPlaylists(musicId: UUID) =
        musicPlaylistDataSource.deleteMusicFromAllPlaylists(
            musicId = musicId
        )
}