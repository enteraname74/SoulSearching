package com.github.enteraname74.domain.usecase.folder

import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.domain.repository.FolderRepository

class DeleteFolderUseCase(
    private val folderRepository: FolderRepository,
) {
    suspend operator fun invoke(folder: Folder) {
        folderRepository.delete(
            folder = folder,
        )
    }
}