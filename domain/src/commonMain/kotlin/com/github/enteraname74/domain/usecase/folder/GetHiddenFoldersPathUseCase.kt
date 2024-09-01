package com.github.enteraname74.domain.usecase.folder

import com.github.enteraname74.domain.repository.FolderRepository
import kotlinx.coroutines.flow.first

class GetHiddenFoldersPathUseCase(
    private val folderRepository: FolderRepository,
) {
    suspend operator fun invoke(): List<String> =
        folderRepository
            .getAll()
            .first()
            .filterNot { it.isSelected }
            .map { it.folderPath }
}