package com.github.enteraname74.localdesktop.datasourceimpl

import com.github.enteraname74.domain.datasource.FolderDataSource
import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.localdesktop.dao.FolderDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Implementation of the FolderDataSource with Exposed.
 */
internal class ExposedFolderDataSourceImpl(
    private val folderDao: FolderDao
): FolderDataSource {
    override suspend fun insertFolder(folder: Folder) = folderDao.insertFolder(folder = folder)

    override suspend fun deleteFolder(folder: Folder) = folderDao.deleteFolder(folder = folder)

    override fun getAllFoldersAsFlow() = folderDao.getAllFoldersAsFlow()

    override suspend fun getAllFolders() = folderDao.getAllFolders()

    override suspend fun getAllHiddenFoldersPaths() = folderDao.getAllHiddenFoldersPaths()
}