package com.github.enteraname74.localdb.dao

import androidx.room.*
import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.localdb.model.RoomPlaylist
import com.github.enteraname74.localdb.model.RoomPlaylistWithMusics
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * DAO of a Playlist.
 */
@Dao
internal interface PlaylistDao {

    @Upsert
    suspend fun upsert(roomPlaylist : RoomPlaylist)

    @Upsert
    suspend fun upsertAll(roomPlaylists : List<RoomPlaylist>)

    @Delete
    suspend fun delete(roomPlaylist : RoomPlaylist)

    @Query("DELETE FROM RoomPlaylist WHERE playlistId IN (:ids) AND isFavorite=0")
    suspend fun deleteAll(ids: List<UUID>)

    @Query("DELETE FROM RoomPlaylist WHERE dataMode = :dataMode AND isFavorite=0")
    suspend fun deleteAll(dataMode: String)

    @Query("SELECT * FROM RoomPlaylist WHERE dataMode = :dataMode ORDER BY name ASC")
    fun getAll(dataMode: String): Flow<List<RoomPlaylist>>

    @Query("SELECT * FROM RoomPlaylist WHERE playlistId IN (:playlistIds) ORDER BY name ASC")
    fun getAll(playlistIds: List<UUID>): List<RoomPlaylist>

    @Transaction
    @Query("SELECT * FROM RoomPlaylist ORDER BY name ASC")
    fun getAllPlaylistWithMusics(): Flow<List<RoomPlaylistWithMusics>>

    @Query("SELECT * FROM RoomPlaylist WHERE playlistId = :playlistId")
    fun getFromId(playlistId: UUID) : Flow<RoomPlaylist?>

    @Transaction
    @Query("SELECT * FROM RoomPlaylist WHERE playlistId = :playlistId")
    fun getPlaylistWithMusics(playlistId : UUID): Flow<RoomPlaylistWithMusics?>

    @Query("SELECT COUNT(*) FROM RoomPlaylist WHERE coverId = :coverId")
    suspend fun getNumberOfPlaylistsWithCoverId(coverId : UUID) : Int

    @Query("UPDATE RoomPlaylist SET isInQuickAccess = :newQuickAccessState WHERE playlistId = :playlistId")
    suspend fun updateQuickAccessState(newQuickAccessState: Boolean, playlistId: UUID)

    @Query("SELECT nbPlayed FROM RoomPlaylist WHERE playlistId = :playlistId")
    suspend fun getNbPlayedOfPlaylist(playlistId: UUID): Int

    @Query("UPDATE RoomPlaylist SET nbPlayed = :newNbPlayed WHERE playlistId = :playlistId")
    suspend fun updateNbPlayed(newNbPlayed: Int, playlistId: UUID)
}