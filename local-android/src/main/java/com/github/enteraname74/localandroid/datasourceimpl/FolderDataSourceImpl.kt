package com.github.enteraname74.localandroid.datasourceimpl

import com.github.enteraname74.localandroid.AppDatabase
import com.github.enteraname74.localandroid.model.toFolder
import com.github.enteraname74.localandroid.model.toRoomFolder
import com.github.enteraname74.domain.datasource.FolderDataSource
import com.github.enteraname74.domain.model.Folder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of the FolderDataSource with Room's DAO.
 */
internal class FolderDataSourceImpl @Inject constructor(
    private val appDatabase: AppDatabase
) : FolderDataSource {
    override suspend fun insertFolder(folder: Folder) {
        appDatabase.folderDao.insertFolder(
            roomFolder = folder.toRoomFolder()
        )
    }

    override suspend fun deleteFolder(folder: Folder) {
        appDatabase.folderDao.deleteFolder(
            roomFolder = folder.toRoomFolder()
        )
    }

    override fun getAllFoldersAsFlow(): Flow<List<Folder>> {
        return appDatabase.folderDao.getAllFoldersAsFlow().map { list ->
            list.map { it.toFolder() }
        }
    }

    override suspend fun getAllFolders(): List<Folder> {
        return appDatabase.folderDao.getAllFolders().map { it.toFolder() }
    }

    override suspend fun getAllHiddenFoldersPaths(): List<String> {
        return appDatabase.folderDao.getAllHiddenFoldersPaths()
    }
}