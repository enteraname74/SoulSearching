package com.github.enteraname74.soulsearching.repository.datasource.musicplaylist

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.MusicPlaylist

/**
 * Data source of MusicPlaylist.
 */
interface MusicPlaylistLocalDataSource {
    /**
     * Inserts or updates a MusicPlaylist.
     * It is the equivalent of adding a Music to a Playlist.
     */
    suspend fun upsert(musicPlaylist: MusicPlaylist)

    suspend fun upsertAll(musicPlaylists: List<MusicPlaylist>)

    /**
     * Deletes a MusicPlaylist.
     * It is the equivalent of removing a Music from a Playlist.
     */
    suspend fun delete(musicPlaylist: MusicPlaylist)
    suspend fun deleteAll(musicPlaylists: List<MusicPlaylist>)
    suspend fun deleteAll(dataMode: DataMode)
}