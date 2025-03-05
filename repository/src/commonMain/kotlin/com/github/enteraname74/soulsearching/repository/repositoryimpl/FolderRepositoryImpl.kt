package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.domain.repository.FolderRepository
import com.github.enteraname74.soulsearching.repository.datasource.FolderDataSource
import kotlinx.coroutines.flow.Flow

/**
 * Repository of a Folder.
 */
class FolderRepositoryImpl(
    private val folderDataSource: FolderDataSource
): FolderRepository {
    override suspend fun upsert(folder: Folder) = folderDataSource.upsert(
        folder = folder
    )

    override suspend fun upsertAll(folders: List<Folder>) {
        folderDataSource.upsertAll(folders)
    }

    override suspend fun delete(folder: Folder) = folderDataSource.delete(
        folder = folder
    )

    override suspend fun deleteAll(folders: List<Folder>) {
        folderDataSource.deleteAll(folders)
    }

    override fun getAll(): Flow<List<Folder>> = folderDataSource.getAll()
}