package com.github.enteraname74.localdesktop.dao

import com.github.enteraname74.domain.model.MusicPlaylist
import java.util.UUID

/**
 * DAO for managing MusicPlaylists.
 */
internal interface MusicPlaylistDao {
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