package com.github.enteraname74.localdb.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Query
import androidx.room3.Upsert
import com.github.enteraname74.localdb.model.RoomFolder
import kotlinx.coroutines.flow.Flow

/**
 * DAO of a Folder.
 */
@Dao
interface FolderDao {
    @Upsert
    suspend fun upsert(roomFolder : RoomFolder)

    @Upsert
    suspend fun upsertAll(roomFolders : List<RoomFolder>)

    @Delete
    suspend fun delete(roomFolder: RoomFolder)

    @Query("DELETE FROM RoomFolder WHERE folderPath IN (:folderPaths)")
    suspend fun deleteAll(folderPaths: List<String>)

    @Query("SELECT * FROM RoomFolder")
    fun getAll(): Flow<List<RoomFolder>>
}
