package com.github.enteraname74.repository

import com.github.enteraname74.datasource.FolderDataSource
import com.github.enteraname74.model.Folder
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Repository of a Folder.
 */
class FolderRepository @Inject constructor(
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