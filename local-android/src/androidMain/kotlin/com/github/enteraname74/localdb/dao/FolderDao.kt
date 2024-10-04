package com.github.enteraname74.localdb.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.github.enteraname74.localdb.model.RoomFolder
import kotlinx.coroutines.flow.Flow

/**
 * DAO of a Folder.
 */
@Dao
internal interface FolderDao {
    @Upsert
    suspend fun upsert(roomFolder : RoomFolder)

    @Upsert
    suspend fun upsertAll(roomFolders : List<RoomFolder>)

    @Delete
    suspend fun delete(roomFolder: RoomFolder)

    @Query("SELECT * FROM RoomFolder")
    fun getAll(): Flow<List<RoomFolder>>
}