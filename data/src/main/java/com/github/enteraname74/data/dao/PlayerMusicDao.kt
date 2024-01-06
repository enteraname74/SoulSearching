package com.github.enteraname74.data.dao

import androidx.room.*
import com.github.enteraname74.data.model.PlayerMusic
import com.github.enteraname74.data.model.PlayerWithMusicItem
import java.util.UUID
import kotlinx.coroutines.flow.Flow

@Dao
internal interface PlayerMusicDao {
    @Upsert
    suspend fun insertPlayerMusic(PlayerMusic: PlayerMusic)

    @Query("DELETE FROM PlayerMusic WHERE playerMusicId = :musicId")
    suspend fun deleteMusicFromPlayerList(musicId : UUID)

    @Query("DELETE FROM PlayerMusic")
    suspend fun deleteAllPlayerMusic()

    @Upsert
    suspend fun insertAllPlayerMusics(playlist: List<PlayerMusic>)

    @Transaction
    @Query("SELECT * FROM PlayerMusic")
    suspend fun getAllPlayerMusics(): List<PlayerWithMusicItem>

    @Transaction
    @Query("SELECT * FROM PlayerMusic")
    fun getAllPlayerMusicsFlow(): Flow<List<PlayerWithMusicItem>>
}