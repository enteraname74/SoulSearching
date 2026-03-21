package com.github.enteraname74.localdb.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.github.enteraname74.localdb.model.RoomCloudPreferences
import kotlinx.coroutines.flow.Flow

@Dao
interface CloudPreferencesDao {
    @Query("SELECT * FROM RoomCloudPreferences LIMIT 1")
    fun observe(): Flow<RoomCloudPreferences?>

    @Upsert
    suspend fun upsert(preferences: RoomCloudPreferences)
}