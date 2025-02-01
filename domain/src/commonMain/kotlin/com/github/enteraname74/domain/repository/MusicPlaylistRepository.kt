package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.model.SoulResult

interface MusicPlaylistRepository {
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
    suspend fun deleteAll(dataMode: DataMode)

    /**
     * Synchronize remote MusicPlaylist links of the users with the cloud
     */
    suspend fun syncWithCloud(): SoulResult<Unit>
}