package com.github.enteraname74.localdb.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.github.enteraname74.localdb.model.RoomUser
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Upsert
    suspend fun upsert(roomUser: RoomUser)

    @Query("SELECT * FROM RoomUser LIMIT 1")
    fun observe(): Flow<RoomUser?>

    @Query("DELETE FROM RoomUser")
    suspend fun clear()
}