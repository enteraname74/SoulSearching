package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.model.Folder
import kotlinx.coroutines.flow.Flow

interface FolderRepository {
    /**
     * Inserts or updates a Folder.
     */
    suspend fun upsert(folder: Folder)

    /**
     * Deletes a Folder.
     */
    suspend fun delete(folder: Folder)

    /**
     * Retrieves a flow of all Folder.
     */
    fun getAll(): Flow<List<Folder>>
}