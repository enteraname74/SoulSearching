package com.github.enteraname74.data.dao

import androidx.room.*
import com.github.enteraname74.data.model.RoomPlaylist
import com.github.enteraname74.data.model.RoomPlaylistWithMusics
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * DAO of a Playlist.
 */
@Dao
internal interface PlaylistDao {

    @Upsert
    suspend fun insertPlaylist(roomPlaylist : RoomPlaylist)

    @Delete
    suspend fun deletePlaylist(roomPlaylist : RoomPlaylist)

    @Query("SELECT * FROM RoomPlaylist ORDER BY name ASC")
    fun getAllPlaylistsSortByNameAscAsFlow(): Flow<List<RoomPlaylist>>

    @Transaction
    @Query("SELECT * FROM RoomPlaylist ORDER BY name ASC")
    fun getAllPlaylistsWithMusicsSortByNameAscAsFlow(): Flow<List<RoomPlaylistWithMusics>>

    @Transaction
    @Query("SELECT * FROM RoomPlaylist ORDER BY name DESC")
    fun getAllPlaylistWithMusicsSortByNameDescAsFlow(): Flow<List<RoomPlaylistWithMusics>>

    @Transaction
    @Query("SELECT * FROM RoomPlaylist ORDER BY addedDate ASC")
    fun getAllPlaylistWithMusicsSortByAddedDateAscAsFlow(): Flow<List<RoomPlaylistWithMusics>>

    @Transaction
    @Query("SELECT * FROM RoomPlaylist ORDER BY addedDate DESC")
    fun getAllPlaylistWithMusicsSortByAddedDateDescAsFlow(): Flow<List<RoomPlaylistWithMusics>>

    @Transaction
    @Query("SELECT * FROM RoomPlaylist ORDER BY nbPlayed ASC")
    fun getAllPlaylistWithMusicsSortByNbPlayedAscAsFlow(): Flow<List<RoomPlaylistWithMusics>>

    @Transaction
    @Query("SELECT * FROM RoomPlaylist ORDER BY nbPlayed DESC")
    fun getAllPlaylistWithMusicsSortByNbPlayedDescAsFlow(): Flow<List<RoomPlaylistWithMusics>>

    @Transaction
    @Query("SELECT * FROM RoomPlaylist WHERE isInQuickAccess = 1")
    fun getAllPlaylistsFromQuickAccessAsFlow(): Flow<List<RoomPlaylistWithMusics>>

    @Query("SELECT * FROM RoomPlaylist WHERE isFavorite = 1")
    suspend fun getFavoritePlaylist(): RoomPlaylist

    @Query("SELECT * FROM RoomPlaylist WHERE playlistId = :playlistId")
    suspend fun getPlaylistFromId(playlistId: UUID) : RoomPlaylist

    @Transaction
    @Query("SELECT * FROM RoomPlaylist WHERE playlistId = :playlistId")
    fun getPlaylistWithMusicsAsFlow(playlistId : UUID): Flow<RoomPlaylistWithMusics?>

    @Transaction
    @Query("SELECT * FROM RoomPlaylist")
    suspend fun getAllPlaylistsWithMusics() : List<RoomPlaylistWithMusics>

    @Query("SELECT COUNT(*) FROM RoomPlaylist WHERE coverId = :coverId")
    suspend fun getNumberOfPlaylistsWithCoverId(coverId : UUID) : Int

    @Query("UPDATE RoomPlaylist SET isInQuickAccess = :newQuickAccessState WHERE playlistId = :playlistId")
    suspend fun updateQuickAccessState(newQuickAccessState: Boolean, playlistId: UUID)

    @Query("SELECT nbPlayed FROM RoomPlaylist WHERE playlistId = :playlistId")
    suspend fun getNbPlayedOfPlaylist(playlistId: UUID): Int

    @Query("UPDATE RoomPlaylist SET nbPlayed = :newNbPlayed WHERE playlistId = :playlistId")
    suspend fun updateNbPlayed(newNbPlayed: Int, playlistId: UUID)
}