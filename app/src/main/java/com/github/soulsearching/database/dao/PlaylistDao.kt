package com.github.soulsearching.database.dao

import androidx.room.*
import com.github.soulsearching.database.model.Playlist
import com.github.soulsearching.database.model.PlaylistWithMusics
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface PlaylistDao {

    @Upsert
    suspend fun insertPlaylist(playlist : Playlist)

    @Delete
    suspend fun deletePlaylist(playlist : Playlist)

    @Query("SELECT * FROM Playlist ORDER BY name ASC")
    fun getAllPlaylists(): Flow<List<Playlist>>

    @Transaction
    @Query("SELECT * FROM Playlist ORDER BY name ASC")
    fun getAllPlaylistsWithMusicsSortByNameAsc(): Flow<List<PlaylistWithMusics>>

    @Transaction
    @Query("SELECT * FROM Playlist ORDER BY name DESC")
    fun getAllPlaylistWithMusicsSortByNameDesc(): Flow<List<PlaylistWithMusics>>

    @Transaction
    @Query("SELECT * FROM Playlist ORDER BY addedDate ASC")
    fun getAllPlaylistWithMusicsSortByAddedDateAsc(): Flow<List<PlaylistWithMusics>>

    @Transaction
    @Query("SELECT * FROM Playlist ORDER BY addedDate DESC")
    fun getAllPlaylistWithMusicsSortByAddedDateDesc(): Flow<List<PlaylistWithMusics>>

    @Transaction
    @Query("SELECT * FROM Playlist ORDER BY nbPlayed ASC")
    fun getAllPlaylistWithMusicsSortByNbPlayedAsc(): Flow<List<PlaylistWithMusics>>

    @Transaction
    @Query("SELECT * FROM Playlist ORDER BY nbPlayed DESC")
    fun getAllPlaylistWithMusicsSortByNbPlayedDesc(): Flow<List<PlaylistWithMusics>>

    @Transaction
    @Query("SELECT * FROM Playlist WHERE isInQuickAccess = TRUE")
    fun getAllPlaylistsFromQuickAccess(): Flow<List<PlaylistWithMusics>>

    @Query("SELECT * FROM Playlist WHERE isFavorite = TRUE")
    fun getFavoritePlaylist(): Playlist

    @Query("SELECT * FROM Playlist WHERE playlistId = :playlistId")
    fun getPlaylistFromId(playlistId: UUID) : Playlist

    @Transaction
    @Query("SELECT * FROM Playlist WHERE playlistId = :playlistId")
    fun getPlaylistWithMusics(playlistId : UUID): Flow<PlaylistWithMusics?>

    @Transaction
    @Query("SELECT * FROM Playlist")
    suspend fun getAllPlaylistsWithMusicsSimple() : List<PlaylistWithMusics>

    @Query("SELECT COUNT(*) FROM Playlist WHERE coverId = :coverId")
    fun getNumberOfPlaylistsWithCoverId(coverId : UUID) : Int

    @Query("UPDATE Playlist SET isInQuickAccess = :newQuickAccessState WHERE playlistId = :playlistId")
    fun updateQuickAccessState(newQuickAccessState: Boolean, playlistId: UUID)

    @Query("SELECT nbPlayed FROM Playlist WHERE playlistId = :playlistId")
    fun getNbPlayedOfPlaylist(playlistId: UUID): Int

    @Query("UPDATE Playlist SET nbPlayed = :newNbPlayed WHERE playlistId = :playlistId")
    fun updateNbPlayed(newNbPlayed: Int, playlistId: UUID)
}