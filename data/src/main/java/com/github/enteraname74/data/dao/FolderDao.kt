package com.github.enteraname74.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.github.enteraname74.data.model.Folder
import kotlinx.coroutines.flow.Flow

@Dao
internal interface FolderDao {
    @Upsert
    suspend fun insertFolder(folder : Folder)

    @Delete
    suspend fun deleteFolder(folder: Folder)

    @Query("SELECT * FROM Folder")
    fun getAllFolders(): Flow<List<Folder>>

    @Query("SELECT * FROM Folder")
    fun getAllFoldersSimple(): List<Folder>

    @Query("SELECT folderPath FROM Folder WHERE isSelected = 0")
    suspend fun getAllHiddenFoldersPaths(): List<String>
}