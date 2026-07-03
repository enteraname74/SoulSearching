package com.github.enteraname74.localdb.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.github.enteraname74.localdb.model.RoomUserInscriptionCode
import kotlinx.coroutines.flow.Flow

@Dao
abstract class UserInscriptionCodeDao {
    @Query("DELETE FROM RoomUserInscriptionCode")
    abstract suspend fun deleteAll()

    @Delete
    abstract suspend fun delete(code: RoomUserInscriptionCode)

    @Upsert
    abstract suspend fun upsertAll(codes: List<RoomUserInscriptionCode>)

    @Transaction
    open suspend fun setCodes(codes: List<RoomUserInscriptionCode>) {
        deleteAll()
        upsertAll(codes)
    }

    @Query("SELECT * FROM RoomUserInscriptionCode")
    abstract fun observeAll(): Flow<List<RoomUserInscriptionCode>>
}
