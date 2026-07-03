package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.soulsearching.repository.datasource.MusicPlaylistDataSource
import kotlin.uuid.Uuid

/**
 * Repository of a MusicPlaylist.
 */
class MusicPlaylistRepositoryImpl(
    private val musicPlaylistDataSource: MusicPlaylistDataSource
): MusicPlaylistRepository {
    override suspend fun upsertMusicIntoPlaylist(musicPlaylist: MusicPlaylist) =
        musicPlaylistDataSource.upsertMusicIntoPlaylist(
            musicPlaylist = musicPlaylist
        )

    override suspend fun deleteMusicFromPlaylist(musicId: Uuid, playlistId: Uuid) =
        musicPlaylistDataSource.deleteMusicFromPlaylist(
            musicId = musicId,
            playlistId = playlistId
        )

    override suspend fun getMusicPlaylist(musicId: Uuid, playlistId: Uuid): MusicPlaylist? =
        musicPlaylistDataSource.getMusicPlaylist(
            musicId = musicId,
            playlistId = playlistId
        )

    override suspend fun deleteMusicFromAllPlaylists(musicId: Uuid) =
        musicPlaylistDataSource.deleteMusicFromAllPlaylists(
            musicId = musicId
        )
}
