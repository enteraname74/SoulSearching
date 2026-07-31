package com.github.enteraname74.soulsearching.repository.datasource.playlist

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.PlaylistPreview
import com.github.enteraname74.domain.model.PlaylistWithMusics
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

/**
 * Data source of a Playlist.
 */
interface PlaylistLocalDataSource {
    /**
     * Inserts or updates a Playlist.
     */
    suspend fun upsert(playlist: Playlist)

    suspend fun upsertAll(playlists: List<Playlist>, keepUpdatedAt: Boolean)

    /**
     * Delete the playlists identified in the given list of ids.
     */
    suspend fun deleteAll(playlistIds: List<Uuid>)

    suspend fun deleteAllFromRemote(remoteIds: List<Uuid>)

    /**
     * Retrieves a flow of all PlaylistWithMusics, sorted by name asc.
     */
    fun getAllPlaylistWithMusics(): Flow<List<PlaylistWithMusics>>

    /**
     * Retrieves a Playlist from its id.
     */
    fun getFromId(playlistId: Uuid): Flow<Playlist?>

    suspend fun getFromRemoteId(remoteId: Uuid): Playlist?

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

    suspend fun getRemoteIdsFromIds(ids: List<Uuid>): List<Uuid>

    suspend fun getAllRemoteIdsPossessedByUser(): List<Uuid>

    suspend fun deleteAllRemoteFields()

    suspend fun deleteAllEmptyExceptFavorite()

    suspend fun getLatestUpdatedAt(): Long?
}
