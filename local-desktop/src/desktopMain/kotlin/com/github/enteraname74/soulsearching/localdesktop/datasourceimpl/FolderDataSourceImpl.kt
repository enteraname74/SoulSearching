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

    override suspend fun delete(folder: Folder) {
        folderDao.delete(folder)
    }

    override fun getAll(): Flow<List<Folder>> =
        folderDao.getAll()
}