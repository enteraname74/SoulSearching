package com.github.enteraname74.domain.repository

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.CloudPlaylist
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.PlaylistPreview
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.model.SoulResult
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface PlaylistRepository {
    /**
     * Inserts or updates a Playlist.
     */
    suspend fun upsert(playlist: Playlist)

    suspend fun upsertAll(playlists: List<Playlist>)

    /**
     * Deletes the playlists identified in the given list of ids.
     */
    suspend fun deleteAll(playlistIds: List<Uuid>): SoulResult<Unit>

    suspend fun deleteAllFromRemote(remoteIds: List<Uuid>)

    /**
     * Retrieves a flow of all PlaylistWithMusics, sorted by name asc.
     */
    fun getAllPlaylistWithMusics(): Flow<List<PlaylistWithMusics>>

    /**
     * Retrieves a Playlist from its id.
     */
    fun getFromId(playlistId: Uuid): Flow<Playlist?>

    fun getFromIds(playlistIds: List<Uuid>): Flow<List<PlaylistWithMusics>>

    suspend fun getFavorite(): Playlist?

    suspend fun getFromName(name: String): Playlist?

    /**
     * Retrieves a flow of a PlaylistWithMusics.
     */
    fun getPlaylistWithMusics(playlistId: Uuid): Flow<PlaylistWithMusics?>

    fun getAllPaged(): Flow<PagingData<PlaylistPreview>>

    suspend fun getAll(page: Int, pageSize: Int): List<PlaylistPreview>

    suspend fun cleanAllCovers()

    fun getAllFromQuickAccess(): Flow<List<PlaylistPreview>>

    fun getMostListened(): Flow<List<PlaylistPreview>>

    fun getPlaylistPreview(playlistId: Uuid): Flow<PlaylistPreview?>

    fun searchAll(search: String): Flow<List<PlaylistPreview>>

    suspend fun getAllToSendToCloud(): List<PlaylistWithMusics>

    suspend fun fetchUpdatedPlaylistsFromCloud(lastSyncMillis: Long?): List<CloudPlaylist>

    suspend fun uploadToCloud(playlistWithMusics: PlaylistWithMusics): CloudPlaylist

    suspend fun getDeletedRemotePlaylistIds(): List<Uuid>
}
