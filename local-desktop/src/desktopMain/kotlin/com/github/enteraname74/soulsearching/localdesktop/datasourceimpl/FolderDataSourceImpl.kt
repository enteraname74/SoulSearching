package com.github.enteraname74.soulsearching.localdesktop.datasourceimpl

import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.soulsearching.localdesktop.dao.FolderDao
import com.github.enteraname74.soulsearching.repository.datasource.FolderDataSource
import kotlinx.coroutines.flow.Flow

internal class FolderDataSourceImpl(
    private val folderDao: FolderDao
) : FolderDataSource {
    override suspend fun upsert(folder: Folder) {
        folderDao.upsert(folder)
    }

    override suspend fun upsertAll(folders: List<Folder>) {
        folderDao.upsertAll(
            folders = folders,
        )
    }

    override suspend fun delete(folder: Folder) {
        folderDao.delete(folder)
    }

    override suspend fun deleteAll(folders: List<Folder>) {
        folderDao.deleteAll(folderPaths = folders.map { it.folderPath })
    }

    override fun getAll(): Flow<List<Folder>> =
        folderDao.getAll()
}