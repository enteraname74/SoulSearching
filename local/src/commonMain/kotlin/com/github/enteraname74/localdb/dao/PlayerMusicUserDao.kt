package com.github.enteraname74.localdb.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Upsert
import com.github.enteraname74.localdb.model.player.RoomCompletePlayerMusicUser
import com.github.enteraname74.localdb.model.player.RoomPlayerMusicUser
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerMusicUserDao {
    @Upsert
    suspend fun upsertAll(playerMusicUsers: List<RoomPlayerMusicUser>)

    @Transaction
    @Query("SELECT * FROM RoomPlayerMusicUser")
    fun observeAll(): Flow<List<RoomCompletePlayerMusicUser>>
}
