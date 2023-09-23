package com.github.soulsearching.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.github.soulsearching.database.model.Folder
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface FolderDao {
    @Upsert
    suspend fun insertFolder(folder : Folder)

    @Delete
    suspend fun deleteFolder(folder: Folder)

    @Query("SELECT * FROM Folder")
    fun getAllFolders(): Flow<List<Folder>>
}