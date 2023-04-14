package com.github.soulsearching.database.dao

import androidx.room.*
import com.github.soulsearching.database.model.Music
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface MusicDao {

    @Upsert
    suspend fun insertMusic(music : Music)

    @Delete
    suspend fun deleteMusic(music : Music)

    @Query("SELECT * FROM Music WHERE musicId = :musicId LIMIT 1")
    fun getMusicFromId(musicId : UUID): Music

    @Query("SELECT * FROM Music ORDER BY name ASC")
    fun getAllMusics(): Flow<List<Music>>
}