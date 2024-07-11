package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.model.MusicPlaylist
import java.util.*

interface MusicPlaylistRepository {
    /**
     * Inserts or updates a MusicPlaylist.
     * It is the equivalent of adding a Music to a Playlist.
     */
    suspend fun upsertMusicIntoPlaylist(musicPlaylist: MusicPlaylist)

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