package com.github.enteraname74.localdb.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Upsert
import com.github.enteraname74.localdb.model.RoomFolder
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.Transient

/**
 * DAO of a Folder.
 */
@Dao
abstract class FolderDao {
    @Upsert
    abstract suspend fun upsert(roomFolder: RoomFolder)

    @Upsert
    abstract suspend fun upsertAll(roomFolders: List<RoomFolder>)

    @Delete
    abstract suspend fun delete(roomFolder: RoomFolder)

    @Query("DELETE FROM RoomFolder WHERE folderPath IN (:folderPaths)")
    abstract suspend fun deleteAll(folderPaths: List<String>)

    @Query("SELECT * FROM RoomFolder")
    abstract fun getAll(): Flow<List<RoomFolder>>

    @Query("DELETE FROM RoomFolder")
    abstract suspend fun clearAll()

    @Transaction
    open suspend fun setAll(folders: List<RoomFolder>) {
        clearAll()
        upsertAll(folders)
    }
}
