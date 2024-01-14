package com.github.enteraname74.localdb.datasourceimpl

import com.github.enteraname74.domain.datasource.FolderDataSource
import com.github.enteraname74.domain.model.Folder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ExposedFolderDataSourceImpl: FolderDataSource {
    override suspend fun insertFolder(folder: Folder) {

    }

    override suspend fun deleteFolder(folder: Folder) {

    }

    override fun getAllFoldersAsFlow(): Flow<List<Folder>> {
        return flowOf(emptyList())
    }

    override suspend fun getAllFolders(): List<Folder> {
        return emptyList()
    }

    override suspend fun getAllHiddenFoldersPaths(): List<String> {
        return emptyList()
    }
}