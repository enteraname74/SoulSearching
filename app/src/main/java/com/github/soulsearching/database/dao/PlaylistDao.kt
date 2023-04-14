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

    @Query("SELECT playlistId FROM Playlist ORDER BY name ASC LIMIT 1")
    fun getFirstPlaylistId(): UUID

    @Query("SELECT * FROM Playlist WHERE playlistId = :playlistId")
    fun getPlaylistFromId(playlistId: UUID) : Playlist

    @Transaction
    @Query("SELECT * FROM Playlist WHERE playlistId = :playlistId")
    fun getPlaylistWithMusics(playlistId : UUID): Flow<PlaylistWithMusics>
}