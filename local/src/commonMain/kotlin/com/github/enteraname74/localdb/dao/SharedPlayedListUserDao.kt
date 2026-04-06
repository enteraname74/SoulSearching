package com.github.enteraname74.localdb.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.github.enteraname74.localdb.model.player.RoomSharedPlayedListUser
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SharedPlayedListUserDao {

    @Upsert
    abstract suspend fun upsertAll(user: List<RoomSharedPlayedListUser>)

    @Query("DELETE FROM RoomSharedPlayedListUser" )
    abstract suspend fun clearAll()

    @Transaction
    open suspend fun setUsers(
        users: List<RoomSharedPlayedListUser>
    ) {
        clearAll()
        upsertAll(users)
    }


    @Query(
        """
            SELECT * FROM RoomSharedPlayedListUser
            ORDER BY joinedAt
        """
    )
    abstract fun getAll(): Flow<List<RoomSharedPlayedListUser>>
}