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
    suspend fun insertFolder(roomFolder : RoomFolder)

    @Delete
    suspend fun deleteFolder(roomFolder: RoomFolder)

    @Query("SELECT * FROM RoomFolder")
    fun getAllFoldersAsFlow(): Flow<List<RoomFolder>>

    @Query("SELECT * FROM RoomFolder")
    suspend fun getAllFolders(): List<RoomFolder>

    @Query("SELECT folderPath FROM RoomFolder WHERE isSelected = 0")
    suspend fun getAllHiddenFoldersPaths(): List<String>
}