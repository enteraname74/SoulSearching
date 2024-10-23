package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.localdb.AppDatabase
import com.github.enteraname74.localdb.model.toFolder
import com.github.enteraname74.localdb.model.toRoomFolder
import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.soulsearching.repository.datasource.FolderDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of the FolderDataSource with Room's DAO.
 */
internal class RoomFolderDataSourceImpl(
    private val appDatabase: AppDatabase
) : FolderDataSource {
    override suspend fun upsert(folder: Folder) {
        appDatabase.folderDao.upsert(
            roomFolder = folder.toRoomFolder()
        )
    }

    override suspend fun upsertAll(folders: List<Folder>) {
        appDatabase.folderDao.upsertAll(folders.map { it.toRoomFolder() })
    }

    override suspend fun delete(folder: Folder) {
        appDatabase.folderDao.delete(
            roomFolder = folder.toRoomFolder()
        )
    }

    override fun getAll(): Flow<List<Folder>> {
        return appDatabase.folderDao.getAll().map { list ->
            list.map { it.toFolder() }
        }
    }
}