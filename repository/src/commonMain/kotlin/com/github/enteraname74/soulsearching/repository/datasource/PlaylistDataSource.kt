package com.github.enteraname74.soulsearching.repository.datasource

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.PlaylistPreview
import com.github.enteraname74.domain.model.PlaylistWithMusics
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Data source of a Playlist.
 */
interface PlaylistDataSource {
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

    /**
     * Retrieves a flow of all PlaylistWithMusics, sorted by name asc.
     */
    fun getAllPlaylistWithMusics(): Flow<List<PlaylistWithMusics>>

    /**
     * Retrieves a Playlist from its id.
     */
    fun getFromId(playlistId: UUID): Flow<Playlist?>

    /**
     * Retrieves a flow of a PlaylistWithMusics.
     */
    fun getPlaylistWithMusics(playlistId: UUID): Flow<PlaylistWithMusics?>

    fun getAllPaged(): Flow<PagingData<PlaylistPreview>>

    suspend fun cleanAllCovers()

    fun getAllFromQuickAccess(): Flow<List<PlaylistPreview>>

    fun getMostListened(): Flow<List<PlaylistPreview>>

    fun getPlaylistPreview(playlistId: UUID): Flow<PlaylistPreview?>
}