package com.github.enteraname74.localdesktop.dao

import com.github.enteraname74.domain.model.Folder
import kotlinx.coroutines.flow.Flow

/**
 * DAO for managing Folders.
 */
internal interface FolderDao {
    /**
     * Inserts or updates a Folder.
     */
    suspend fun insertFolder(folder : Folder)

    /**
     * Deletes a Folder.
     */
    suspend fun deleteFolder(folder: Folder)

    /**
     * Retrieves a flow of all Folder.
     */
    fun getAllFoldersAsFlow(): Flow<List<Folder>>

    /**
     * Retrieves all Folder.
     */
    suspend fun getAllFolders(): List<Folder>

    /**
     * Retrieves all hidden (not used) Folder's path.
     */
    suspend fun getAllHiddenFoldersPaths(): List<String>
}