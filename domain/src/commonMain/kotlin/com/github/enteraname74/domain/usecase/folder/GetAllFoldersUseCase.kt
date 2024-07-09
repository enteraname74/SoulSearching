package com.github.enteraname74.domain.usecase.folder

import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.domain.repository.FolderRepository
import kotlinx.coroutines.flow.Flow

class GetAllFoldersUseCase(
    private val folderRepository: FolderRepository,
) {
    operator fun invoke(): Flow<List<Folder>> =
        folderRepository.getAll()
}