package com.github.enteraname74.localdb.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import com.github.enteraname74.localdb.model.RoomSoulSearchingSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM RoomSoulSearchingSettings WHERE id = 'SoulSearchingSettings'")
    fun observe(): Flow<RoomSoulSearchingSettings>

    @Query("SELECT * FROM RoomSoulSearchingSettings WHERE id = 'SoulSearchingSettings'")
    suspend fun get(): RoomSoulSearchingSettings

    @Upsert
    suspend fun upsert(settings: RoomSoulSearchingSettings)
}
