package com.github.enteraname74.domain.usecase.folder

import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.domain.repository.FolderRepository

class DeleteAllFoldersUseCase(
    private val folderRepository: FolderRepository
) {
    suspend operator fun invoke(folders: List<Folder>) =
        folderRepository.deleteAll(folders = folders)
}