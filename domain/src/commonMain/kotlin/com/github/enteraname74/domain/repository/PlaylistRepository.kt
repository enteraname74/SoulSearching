package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.model.SoulResult
import kotlinx.coroutines.flow.Flow
import java.util.*

interface PlaylistRepository {
    suspend fun create(playlist: Playlist): SoulResult<Unit>
    suspend fun upsertAll(playlists: List<Playlist>)

    suspend fun update(playlist: Playlist): SoulResult<Unit>
    /**
     * Deletes a Playlist.
     */
    suspend fun delete(playlist: Playlist): SoulResult<Unit>

    /**
     * Deletes the playlists identified in the given list of ids.
     */
    suspend fun deleteAll(playlistIds: List<UUID>): SoulResult<Unit>
    suspend fun deleteAll(dataMode: DataMode)

    /**
     * Retrieves a flow of all Playlist, sorted by name asc.
     */
    fun getAll(dataMode: DataMode? = null): Flow<List<Playlist>>

    /**
     * Retrieves a flow of all PlaylistWithMusics, sorted by name asc.
     */
    fun getAllPlaylistWithMusics(dataMode: DataMode? = null): Flow<List<PlaylistWithMusics>>

    /**
     * Retrieves a Playlist from its id.
     */
    fun getFromId(playlistId: UUID): Flow<Playlist?>

    /**
     * Retrieves a flow of a PlaylistWithMusics.
     */
    fun getPlaylistWithMusics(playlistId: UUID): Flow<PlaylistWithMusics?>

    suspend fun syncWithCloud(): SoulResult<Unit>

    suspend fun uploadAllToCloud(
        localMusicsToRemote: Map<UUID, UUID>,
    ): SoulResult<Unit>
}