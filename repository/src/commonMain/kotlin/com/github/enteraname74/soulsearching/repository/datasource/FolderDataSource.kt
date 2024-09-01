package com.github.enteraname74.soulsearching.repository.datasource

import com.github.enteraname74.domain.model.Folder
import kotlinx.coroutines.flow.Flow

/**
 * Data source of a Folder
 */
interface FolderDataSource {
    /**
     * Inserts or updates a Folder.
     */
    suspend fun upsert(folder : Folder)

    /**
     * Deletes a Folder.
     */
    suspend fun delete(folder: Folder)

    /**
     * Retrieves a flow of all Folder.
     */
    fun getAll(): Flow<List<Folder>>
}