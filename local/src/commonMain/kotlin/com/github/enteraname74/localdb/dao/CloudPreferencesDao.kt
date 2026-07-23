package com.github.enteraname74.localdb.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import com.github.enteraname74.localdb.model.RoomCloudPreferences
import kotlinx.coroutines.flow.Flow

@Dao
interface CloudPreferencesDao {
    @Query("SELECT * FROM RoomCloudPreferences LIMIT 1")
    fun observe(): Flow<RoomCloudPreferences?>

    @Upsert
    suspend fun upsert(preferences: RoomCloudPreferences)

    @Query("UPDATE RoomCloudPreferences SET lastSyncMillis = NULL")
    suspend fun clearLastSyncMillis()
}
