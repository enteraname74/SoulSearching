package com.github.enteraname74.localdb.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Upsert
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
            ORDER BY joinedAt DESC
        """
    )
    abstract fun getAll(): Flow<List<RoomSharedPlayedListUser>>
}
