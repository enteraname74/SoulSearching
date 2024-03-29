package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.datasource.MusicPlaylistDataSource
import com.github.enteraname74.domain.model.MusicPlaylist
import java.util.UUID
import javax.inject.Inject

/**
 * Repository of a MusicPlaylist.
 */
class MusicPlaylistRepository @Inject constructor(
    private val musicPlaylistDataSource: MusicPlaylistDataSource
) {
    /**
     * Inserts or updates a MusicPlaylist.
     * It is the equivalent of adding a Music to a Playlist.
     */
    suspend fun insertMusicIntoPlaylist(musicPlaylist: MusicPlaylist) =
        musicPlaylistDataSource.insertMusicIntoPlaylist(
            musicPlaylist = musicPlaylist
        )

    /**
     * Deletes a MusicPlaylist.
     * It is the equivalent of removing a Music from a Playlist.
     */
    suspend fun deleteMusicFromPlaylist(musicId: UUID, playlistId: UUID) =
        musicPlaylistDataSource.deleteMusicFromPlaylist(
            musicId = musicId,
            playlistId = playlistId
        )

    /**
     * Tries to retrieve a MusicPlaylist from given information.
     */
    suspend fun getMusicPlaylist(musicId: UUID, playlistId: UUID): MusicPlaylist? =
        musicPlaylistDataSource.getMusicPlaylist(
            musicId = musicId,
            playlistId = playlistId
        )

    /**
     * Deletes a Music from all playlists.
     */
    suspend fun deleteMusicFromAllPlaylists(musicId: UUID) =
        musicPlaylistDataSource.deleteMusicFromAllPlaylists(
            musicId = musicId
        )
}