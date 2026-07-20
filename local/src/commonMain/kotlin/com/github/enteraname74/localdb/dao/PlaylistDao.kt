package com.github.enteraname74.localdb.dao

import androidx.paging.PagingSource
import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Upsert
import com.github.enteraname74.localdb.model.RoomPlaylist
import com.github.enteraname74.localdb.model.RoomPlaylistWithMusics
import com.github.enteraname74.localdb.view.RoomPlaylistPreview
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

/**
 * DAO of a Playlist.
 */
@Dao
interface PlaylistDao {

    @Upsert
    suspend fun upsert(roomPlaylist: RoomPlaylist)

    @Upsert
    suspend fun upsertAll(roomPlaylists: List<RoomPlaylist>)

    @Query("DELETE FROM RoomPlaylist WHERE playlistId IN (:ids) AND isFavorite = 0")
    suspend fun deleteAll(ids: List<Uuid>)

    @Query("DELETE FROM RoomPlaylist WHERE remoteId IN (:ids) AND isFavorite = 0")
    suspend fun deleteAllFromRemote(remoteIds: List<Uuid>)

    @Transaction
    @Query("SELECT * FROM RoomPlaylist ORDER BY name ASC")
    fun getAllPlaylistWithMusics(): Flow<List<RoomPlaylistWithMusics>>

    @Query("SELECT * FROM RoomPlaylist WHERE playlistId = :playlistId LIMIT 1")
    fun getFromId(playlistId: Uuid): Flow<RoomPlaylist?>

    @Transaction
    @Query("SELECT * FROM RoomPlaylist WHERE playlistId IN (:playlistIds)")
    fun getFromIds(playlistIds: List<Uuid>): Flow<List<RoomPlaylistWithMusics>>

    @Query("SELECT * FROM RoomPlaylist WHERE isFavorite = 1 LIMIT 1")
    suspend fun getFavorite(): RoomPlaylist?

    @Query("SELECT * FROM RoomPlaylist WHERE name = :name LIMIT 1")
    suspend fun getFromName(name: String): RoomPlaylist?

    @Transaction
    @Query("SELECT * FROM RoomPlaylist WHERE playlistId = :playlistId")
    fun getPlaylistWithMusics(playlistId: Uuid): Flow<RoomPlaylistWithMusics?>

    @Query("UPDATE RoomPlaylist SET coverId = NULL")
    suspend fun cleanAllCovers()

    @Transaction
    @Query(
        """
            SELECT * FROM RoomPlaylistPreview 
            ORDER BY name ASC
        """
    )
    fun getAllPagedByNameAsc(): PagingSource<Int, RoomPlaylistPreview>

    @Transaction
    @Query(
        """
            SELECT * FROM RoomPlaylistPreview 
            ORDER BY name DESC
        """
    )
    fun getAllPagedByNameDesc(): PagingSource<Int, RoomPlaylistPreview>

    @Transaction
    @Query(
        """
            SELECT * FROM RoomPlaylistPreview 
            ORDER BY addedDate ASC
        """
    )
    fun getAllPagedByDateAsc(): PagingSource<Int, RoomPlaylistPreview>

    @Transaction
    @Query(
        """
            SELECT * FROM RoomPlaylistPreview 
            ORDER BY addedDate DESC
        """
    )
    fun getAllPagedByDateDesc(): PagingSource<Int, RoomPlaylistPreview>

    @Transaction
    @Query(
        """
            SELECT * FROM RoomPlaylistPreview 
            ORDER BY nbPlayed ASC
        """
    )
    fun getAllPagedByNbPlayedAsc(): PagingSource<Int, RoomPlaylistPreview>

    @Transaction
    @Query(
        """
            SELECT * FROM RoomPlaylistPreview 
            ORDER BY nbPlayed DESC
        """
    )
    fun getAllPagedByNbPlayedDesc(): PagingSource<Int, RoomPlaylistPreview>

    @Transaction
    @Query(
        """
            SELECT * FROM RoomPlaylistPreview 
            WHERE isInQuickAccess = 1
        """
    )
    fun getAllFromQuickAccess(): Flow<List<RoomPlaylistPreview>>

    @Transaction
    @Query(
        """
            SELECT * FROM RoomPlaylistPreview 
            WHERE nbPlayed >= 1 
            ORDER BY nbPlayed DESC 
            LIMIT 11
        """
    )
    fun getMostListened(): Flow<List<RoomPlaylistPreview>>

    @Transaction
    @Query(
        """
            SELECT * FROM RoomPlaylistPreview 
            WHERE id = :playlistId 
            LIMIT 1
        """
    )
    fun getPlaylistPreview(playlistId: Uuid): Flow<RoomPlaylistPreview?>

    // TODO: Normalise with accents.
    @Transaction
    @Query(
        """
            SELECT * FROM RoomPlaylistPreview 
            WHERE name LIKE '%' || :search || '%' COLLATE NOCASE 
        """
    )
    fun searchAll(search: String): Flow<List<RoomPlaylistPreview>>

    @Query(
        """
            UPDATE RoomPlaylist 
            SET lastUpdatedMillis = :updatedAt 
            WHERE playlistId IN (:playlistIds)
        """
    )
    suspend fun updateLastUpdatedAtField(
        playlistIds: List<Uuid>,
        updatedAt: Long,
    )

    @Query(
        """
            SELECT p.* FROM RoomPlaylist p 
            CROSS JOIN RoomCloudPreferences cp 
            WHERE p.lastUpdatedMillis IS NULL 
                OR cp.lastSyncMillis IS NULL
                OR p.lastUpdatedMillis > cp.lastSyncMillis 
                OR p.remoteId IS NULL
        """
    )
    suspend fun getAllToSendToCloud(): List<RoomPlaylistWithMusics>

    @Query("SELECT remoteId FROM RoomPlaylist WHERE playlistId IN (:ids) AND remoteId IS NOT NULL")
    suspend fun getRemoteIdsFromIds(ids: List<Uuid>): List<Uuid>

    @Query(
        """
            SELECT remoteId FROM RoomPlaylist 
            WHERE remoteId IS NOT NULL
        """
    )
    suspend fun getAllRemoteIdsPossessedByUser(): List<Uuid>
}
