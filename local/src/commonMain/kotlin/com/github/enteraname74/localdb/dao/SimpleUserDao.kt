package com.github.enteraname74.localdb.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.github.enteraname74.localdb.model.RoomSimpleUser
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

@Dao
abstract class SimpleUserDao {
    @Query("SELECT * FROM RoomSimpleUser")
    abstract fun observeAll(): Flow<List<RoomSimpleUser>>

    @Upsert
    abstract suspend fun upsertAll(users: List<RoomSimpleUser>)

    @Query("DELETE FROM RoomSimpleUser")
    abstract suspend fun clearAll()

    @Transaction
    open suspend fun setUsers(users: List<RoomSimpleUser>) {
        clearAll()
        upsertAll(users)
    }

    @Query("DELETE FROM RoomSimpleUser WHERE id = :userId")
    abstract suspend fun delete(userId: Uuid)
}
