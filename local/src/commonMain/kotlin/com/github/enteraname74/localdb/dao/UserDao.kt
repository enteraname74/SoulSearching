package com.github.enteraname74.localdb.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import com.github.enteraname74.localdb.model.RoomUser
import com.github.enteraname74.localdb.model.RoomUserStorage
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Upsert
    suspend fun upsert(roomUser: RoomUser)

    @Query("SELECT * FROM RoomUser LIMIT 1")
    fun observe(): Flow<RoomUser?>

    @Query("DELETE FROM RoomUser")
    suspend fun clear()

    @Query("DELETE FROM RoomUserStorage")
    suspend fun clearUserStorage()

    @Upsert
    suspend fun saveUserStorage(userStorage: RoomUserStorage)

    @Query("SELECT * FROM RoomUserStorage LIMIT 1")
    fun observeUserStorage(): Flow<RoomUserStorage?>
}
