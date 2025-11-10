package com.github.enteraname74.localdb.dao

import androidx.room.*
import com.github.enteraname74.localdb.model.RoomPlaylist
import com.github.enteraname74.localdb.model.RoomPlaylistWithMusics
import kotlinx.coroutines.flow.Flow
import java.util.*

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

    @Query("SELECT * FROM RoomPlaylist ORDER BY name ASC")
    fun getAll(): Flow<List<RoomPlaylist>>

    @Transaction
    @Query("SELECT * FROM RoomPlaylist ORDER BY name ASC")
    fun getAllPlaylistWithMusics(): Flow<List<RoomPlaylistWithMusics>>

    @Query("SELECT * FROM RoomPlaylist WHERE playlistId = :playlistId")
    fun getFromId(playlistId: UUID) : Flow<RoomPlaylist?>

    @Transaction
    @Query("SELECT * FROM RoomPlaylist WHERE playlistId = :playlistId")
    fun getPlaylistWithMusics(playlistId : UUID): Flow<RoomPlaylistWithMusics?>

    @Transaction
    @Query("SELECT * FROM RoomPlaylist")
    suspend fun getAllPlaylistsWithMusics() : List<RoomPlaylistWithMusics>

    @Query("SELECT COUNT(*) FROM RoomPlaylist WHERE coverId = :coverId")
    suspend fun getNumberOfPlaylistsWithCoverId(coverId : UUID) : Int
}