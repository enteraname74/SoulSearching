package com.github.enteraname74.localdb.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.github.enteraname74.localdb.model.RoomPlaylist
import com.github.enteraname74.localdb.model.RoomPlaylistPreview
import com.github.enteraname74.localdb.model.RoomPlaylistWithMusics
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * DAO of a Playlist.
 */
@Dao
interface PlaylistDao {

    @Upsert
    suspend fun upsert(roomPlaylist : RoomPlaylist)

    @Upsert
    suspend fun upsertAll(roomPlaylists : List<RoomPlaylist>)

    @Delete
    suspend fun delete(roomPlaylist : RoomPlaylist)

    @Query("DELETE FROM RoomPlaylist WHERE playlistId IN (:ids) AND isFavorite=0")
    suspend fun deleteAll(ids: List<UUID>)

    @Transaction
    @Query("SELECT * FROM RoomPlaylist ORDER BY name ASC")
    fun getAllPlaylistWithMusics(): Flow<List<RoomPlaylistWithMusics>>

    @Query("SELECT * FROM RoomPlaylist WHERE playlistId = :playlistId")
    fun getFromId(playlistId: UUID) : Flow<RoomPlaylist?>

    @Transaction
    @Query("SELECT * FROM RoomPlaylist WHERE playlistId = :playlistId")
    fun getPlaylistWithMusics(playlistId : UUID): Flow<RoomPlaylistWithMusics?>

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
    fun getPlaylistPreview(playlistId: UUID): Flow<RoomPlaylistPreview?>

    // TODO: Normalise with accents.
    @Transaction
    @Query(
        """
            SELECT * FROM RoomPlaylistPreview 
            WHERE name LIKE '%' || :search || '%' COLLATE NOCASE 
        """
    )
    fun searchAll(search: String): Flow<List<RoomPlaylistPreview>>
}