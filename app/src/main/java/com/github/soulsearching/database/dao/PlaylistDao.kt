package com.github.soulsearching.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.github.soulsearching.database.model.Music
import com.github.soulsearching.database.model.Playlist
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Upsert
    suspend fun insertPlaylist(playlist : Playlist)

    @Delete
    suspend fun deletePlaylist(playlist : Playlist)

    @Query("SELECT * FROM Playlist ORDER BY name ASC")
    fun getAllPlaylists(): Flow<List<Playlist>>
}