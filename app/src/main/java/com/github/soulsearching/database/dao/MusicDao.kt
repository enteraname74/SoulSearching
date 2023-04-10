package com.github.soulsearching.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.github.soulsearching.database.model.Music
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {

    @Upsert
    suspend fun insertMusic(music : Music)

    @Delete
    suspend fun deleteMusic(music : Music)

    @Query("SELECT * FROM Music ORDER BY name ASC")
    fun getAllMusics(): Flow<List<Music>>
}