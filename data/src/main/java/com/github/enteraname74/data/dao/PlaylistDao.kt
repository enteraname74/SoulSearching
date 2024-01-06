package com.github.enteraname74.data.dao

import androidx.room.*
import com.github.enteraname74.data.model.RoomPlaylist
import com.github.enteraname74.data.model.RoomPlaylistWithMusics
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
internal interface PlaylistDao {

    @Upsert
    suspend fun insertPlaylist(roomPlaylist : RoomPlaylist)

    @Delete
    suspend fun deletePlaylist(roomPlaylist : RoomPlaylist)

    @Query("SELECT * FROM RoomPlaylist ORDER BY name ASC")
    fun getAllPlaylists(): Flow<List<RoomPlaylist>>

    @Transaction
    @Query("SELECT * FROM RoomPlaylist ORDER BY name ASC")
    fun getAllPlaylistsWithMusicsSortByNameAsc(): Flow<List<RoomPlaylistWithMusics>>

    @Transaction
    @Query("SELECT * FROM RoomPlaylist ORDER BY name DESC")
    fun getAllPlaylistWithMusicsSortByNameDesc(): Flow<List<RoomPlaylistWithMusics>>

    @Transaction
    @Query("SELECT * FROM RoomPlaylist ORDER BY addedDate ASC")
    fun getAllPlaylistWithMusicsSortByAddedDateAsc(): Flow<List<RoomPlaylistWithMusics>>

    @Transaction
    @Query("SELECT * FROM RoomPlaylist ORDER BY addedDate DESC")
    fun getAllPlaylistWithMusicsSortByAddedDateDesc(): Flow<List<RoomPlaylistWithMusics>>

    @Transaction
    @Query("SELECT * FROM RoomPlaylist ORDER BY nbPlayed ASC")
    fun getAllPlaylistWithMusicsSortByNbPlayedAsc(): Flow<List<RoomPlaylistWithMusics>>

    @Transaction
    @Query("SELECT * FROM RoomPlaylist ORDER BY nbPlayed DESC")
    fun getAllPlaylistWithMusicsSortByNbPlayedDesc(): Flow<List<RoomPlaylistWithMusics>>

    @Transaction
    @Query("SELECT * FROM RoomPlaylist WHERE isInQuickAccess = 1")
    fun getAllPlaylistsFromQuickAccess(): Flow<List<RoomPlaylistWithMusics>>

    @Query("SELECT * FROM RoomPlaylist WHERE isFavorite = 1")
    fun getFavoritePlaylist(): RoomPlaylist

    @Query("SELECT * FROM RoomPlaylist WHERE playlistId = :playlistId")
    fun getPlaylistFromId(playlistId: UUID) : RoomPlaylist

    @Transaction
    @Query("SELECT * FROM RoomPlaylist WHERE playlistId = :playlistId")
    fun getPlaylistWithMusics(playlistId : UUID): Flow<RoomPlaylistWithMusics?>

    @Transaction
    @Query("SELECT * FROM RoomPlaylist")
    suspend fun getAllPlaylistsWithMusicsSimple() : List<RoomPlaylistWithMusics>

    @Query("SELECT COUNT(*) FROM RoomPlaylist WHERE coverId = :coverId")
    fun getNumberOfPlaylistsWithCoverId(coverId : UUID) : Int

    @Query("UPDATE RoomPlaylist SET isInQuickAccess = :newQuickAccessState WHERE playlistId = :playlistId")
    fun updateQuickAccessState(newQuickAccessState: Boolean, playlistId: UUID)

    @Query("SELECT nbPlayed FROM RoomPlaylist WHERE playlistId = :playlistId")
    fun getNbPlayedOfPlaylist(playlistId: UUID): Int

    @Query("UPDATE RoomPlaylist SET nbPlayed = :newNbPlayed WHERE playlistId = :playlistId")
    fun updateNbPlayed(newNbPlayed: Int, playlistId: UUID)
}