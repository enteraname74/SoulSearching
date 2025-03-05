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
    suspend fun upsert(roomPlayerMusic: RoomPlayerMusic)

    @Query("DELETE FROM RoomPlayerMusic WHERE playerMusicId = :musicId")
    suspend fun delete(musicId : UUID)

    @Query("DELETE FROM RoomPlayerMusic")
    suspend fun deleteAll()

    @Upsert
    suspend fun upsertAll(playlist: List<RoomPlayerMusic>)

    @Transaction
    @Query("SELECT * FROM RoomPlayerMusic")
    fun getAll(): Flow<List<RoomPlayerWithMusicItem>>
}