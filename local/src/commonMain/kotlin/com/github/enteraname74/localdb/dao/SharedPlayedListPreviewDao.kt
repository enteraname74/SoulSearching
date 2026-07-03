package com.github.enteraname74.localdb.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.github.enteraname74.localdb.model.player.RoomSharedPlayedListPreview
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SharedPlayedListPreviewDao {

    @Query("SELECT * FROM RoomSharedPlayedListPreview ORDER BY createdAtMillis DESC")
    abstract fun observeAll(): Flow<List<RoomSharedPlayedListPreview>>

    @Query("DELETE FROM RoomSharedPlayedListPreview")
    abstract suspend fun deleteAll()

    @Upsert
    abstract suspend fun upsertAll(previews: List<RoomSharedPlayedListPreview>)

    @Transaction
    open suspend fun setPreviews(previews: List<RoomSharedPlayedListPreview>) {
        deleteAll()
        upsertAll(previews)
    }
}
