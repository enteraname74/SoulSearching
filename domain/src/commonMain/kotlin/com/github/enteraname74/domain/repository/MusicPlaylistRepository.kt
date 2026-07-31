package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.model.SoulResult
import kotlin.uuid.Uuid

interface MusicPlaylistRepository {
    /**
     * Inserts or updates a MusicPlaylist.
     * It is the equivalent of adding a Music to a Playlist.
     */
    suspend fun upsertMusicIntoPlaylist(musicPlaylist: MusicPlaylist)

    suspend fun upsertAll(musicPlaylists: List<MusicPlaylist>, keepUpdatedAt: Boolean)

    suspend fun deleteFromPlaylist(musicIds: List<Uuid>, playlistId: Uuid)

    /**
     * Tries to retrieve a MusicPlaylist from given information.
     */
    suspend fun getMusicPlaylist(musicId: Uuid, playlistId: Uuid): MusicPlaylist?

    /**
     * Deletes a Music from all playlists.
     */
    suspend fun deleteMusicFromAllPlaylists(musicId: Uuid)
}
