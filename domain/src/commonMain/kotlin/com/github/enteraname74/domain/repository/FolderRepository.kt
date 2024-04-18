package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.datasource.FolderDataSource
import com.github.enteraname74.domain.model.Folder
import kotlinx.coroutines.flow.Flow

/**
 * Repository of a Folder.
 */
class FolderRepository(
    private val folderDataSource: FolderDataSource
) {
    /**
     * Inserts or updates a Folder.
     */
    suspend fun insertFolder(folder: Folder) = folderDataSource.insertFolder(
        folder = folder
    )

    /**
     * Deletes a Folder.
     */
    suspend fun deleteFolder(folder: Folder) = folderDataSource.deleteFolder(
        folder = folder
    )

    /**
     * Retrieves a flow of all Folder.
     */
    fun getAllFoldersAsFlow(): Flow<List<Folder>> = folderDataSource.getAllFoldersAsFlow()

    /**
     * Retrieves all Folder.
     */
    suspend fun getAllFolders(): List<Folder> = folderDataSource.getAllFolders()

    /**
     * Retrieves all hidden (not used) Folder's path.
     */
    suspend fun getAllHiddenFoldersPaths(): List<String> =
        folderDataSource.getAllHiddenFoldersPaths()
}