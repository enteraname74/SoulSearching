package com.github.enteraname74.localdesktop.daoimpl

import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.localdesktop.dao.FolderDao
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of the FolderDao for Exposed.
 */
class ExposedFolderDaoImpl: FolderDao{
    override suspend fun insertFolder(folder: Folder) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFolder(folder: Folder) {
        TODO("Not yet implemented")
    }

    override fun getAllFoldersAsFlow(): Flow<List<Folder>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllFolders(): List<Folder> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllHiddenFoldersPaths(): List<String> {
        TODO("Not yet implemented")
    }
}