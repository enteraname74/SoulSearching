package com.github.enteraname74.domain.usecase.folder

import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.domain.repository.FolderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class CommonFolderUseCase(
    private val folderRepository: FolderRepository,
) {
    suspend fun deleteAll(folders: List<Folder>) =
        folderRepository.deleteAll(folders = folders)

    fun getAll(): Flow<List<Folder>> =
        folderRepository.getAll()

    suspend fun getHiddenFoldersPath(): List<String> =
        folderRepository
            .getAll()
            .first()
            .filterNot { it.isSelected }
            .map { it.folderPath }

    suspend fun upsertAll(allFolders: List<Folder>) {
        folderRepository.upsertAll(allFolders)
    }

    suspend fun upsert(folder: Folder) {
        folderRepository.upsert(
            folder = folder
        )
    }
}