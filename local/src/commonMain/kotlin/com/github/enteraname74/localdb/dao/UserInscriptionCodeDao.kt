package com.github.enteraname74.localdb.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Upsert
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
