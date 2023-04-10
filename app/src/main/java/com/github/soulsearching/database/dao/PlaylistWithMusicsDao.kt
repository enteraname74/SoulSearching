package com.github.soulsearching.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.github.soulsearching.database.model.PlaylistWithMusics
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistWithMusicsDao {
    @Transaction
    @Query("SELECT * FROM Playlist")
    fun getPlaylistsWithSongs(): Flow<List<PlaylistWithMusics>>
}