package com.github.enteraname74.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.github.enteraname74.data.model.Room
import kotlinx.coroutines.flow.Flow

@Dao
internal interface FolderDao {
    @Upsert
    suspend fun insertFolder(room : Room)

    @Delete
    suspend fun deleteFolder(room: Room)

    @Query("SELECT * FROM Room")
    fun getAllFolders(): Flow<List<Room>>

    @Query("SELECT * FROM Room")
    fun getAllFoldersSimple(): List<Room>

    @Query("SELECT folderPath FROM Room WHERE isSelected = 0")
    suspend fun getAllHiddenFoldersPaths(): List<String>
}