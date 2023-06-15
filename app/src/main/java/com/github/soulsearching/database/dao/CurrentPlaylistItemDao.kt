package com.github.soulsearching.database.dao

import androidx.room.*
import com.github.soulsearching.database.model.CurrentPlaylistItem

@Dao
interface CurrentPlaylistItemDao {
    @Upsert
    suspend fun insertCurrentPlaylistItem(currentPlaylistItem: CurrentPlaylistItem)

    @Delete
    suspend fun deleteCurrentPlaylistItem(currentPlaylistItem: CurrentPlaylistItem)

    @Query("DELETE FROM CurrentPlaylistItem")
    suspend fun deleteAllCurrentPlaylistItem()

    @Upsert
    suspend fun insertAll(playlist: List<CurrentPlaylistItem>)

    @Query("SELECT * FROM CurrentPlaylistItem")
    suspend fun getAll(): List<CurrentPlaylistItem>
}