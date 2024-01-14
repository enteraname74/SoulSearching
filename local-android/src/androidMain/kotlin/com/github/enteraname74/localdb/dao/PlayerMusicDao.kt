package com.github.enteraname74.localdb.dao

import androidx.room.*
import com.github.enteraname74.localdb.model.RoomPlayerMusic
import com.github.enteraname74.localdb.model.RoomPlayerWithMusicItem
import java.util.UUID
import kotlinx.coroutines.flow.Flow

/**
 * DAO of a PlayerMusic
 */
@Dao
internal interface PlayerMusicDao {
    @Upsert
    suspend fun insertPlayerMusic(roomPlayerMusic: RoomPlayerMusic)

    @Query("DELETE FROM RoomPlayerMusic WHERE playerMusicId = :musicId")
    suspend fun deleteMusicFromPlayerList(musicId : UUID)

    @Query("DELETE FROM RoomPlayerMusic")
    suspend fun deleteAllPlayerMusic()

    @Upsert
    suspend fun insertAllPlayerMusics(playlist: List<RoomPlayerMusic>)

    @Transaction
    @Query("SELECT * FROM RoomPlayerMusic")
    suspend fun getAllPlayerMusics(): List<RoomPlayerWithMusicItem>

    @Transaction
    @Query("SELECT * FROM RoomPlayerMusic")
    fun getAllPlayerMusicsAsFlow(): Flow<List<RoomPlayerWithMusicItem>>
}