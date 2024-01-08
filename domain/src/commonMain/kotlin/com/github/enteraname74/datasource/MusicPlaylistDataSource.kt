package com.github.enteraname74.datasource

import com.github.enteraname74.model.MusicPlaylist
import java.util.UUID

/**
 * Data source of MusicPlaylist.
 */
interface MusicPlaylistDataSource {
    /**
     * Inserts or updates a MusicPlaylist.
     * It is the equivalent of adding a Music to a Playlist.
     */
    suspend fun insertMusicIntoPlaylist(musicPlaylist: MusicPlaylist)

    /**
     * Deletes a MusicPlaylist.
     * It is the equivalent of removing a Music from a Playlist.
     */
    suspend fun deleteMusicFromPlaylist(musicId: UUID, playlistId: UUID)

    /**
     * Tries to retrieve a MusicPlaylist from given information.
     */
    suspend fun getMusicPlaylist(musicId: UUID, playlistId: UUID): MusicPlaylist?

    /**
     * Deletes a Music from all playlists.
     */
    suspend fun deleteMusicFromAllPlaylists(musicId: UUID)
}