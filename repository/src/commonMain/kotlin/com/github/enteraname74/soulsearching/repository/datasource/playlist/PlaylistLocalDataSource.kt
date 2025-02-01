package com.github.enteraname74.soulsearching.repository.datasource.playlist

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.PlaylistWithMusics
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Data source of a Playlist.
 */
interface PlaylistLocalDataSource {
    /**
     * Inserts or updates a Playlist.
     */
    suspend fun upsert(playlist: Playlist)

    suspend fun upsertAll(playlists: List<Playlist>)

    /**
     * Deletes a Playlist.
     */
    suspend fun delete(playlist: Playlist)

    /**
     * Delete the playlists identified in the given list of ids.
     */
    suspend fun deleteAll(playlistIds: List<UUID>)
    suspend fun deleteAll(dataMode: DataMode)

    /**
     * Retrieves a flow of all Playlist, sorted by name asc.
     */
    fun getAll(dataMode: DataMode): Flow<List<Playlist>>
    suspend fun getAll(playlistIds: List<UUID>): List<Playlist>

    /**
     * Retrieves a flow of all PlaylistWithMusics, sorted by name asc.
     */
    fun getAllPlaylistWithMusics(dataMode: DataMode): Flow<List<PlaylistWithMusics>>

    /**
     * Retrieves a Playlist from its id.
     */
    fun getFromId(playlistId: UUID): Flow<Playlist?>

    /**
     * Retrieves a flow of a PlaylistWithMusics.
     */
    fun getPlaylistWithMusics(playlistId: UUID): Flow<PlaylistWithMusics?>
}