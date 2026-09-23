package com.github.enteraname74.soulsearching.domain.usecase.music

import com.github.enteraname74.soulsearching.domain.model.Folder
import com.github.enteraname74.soulsearching.domain.model.Music
import com.github.enteraname74.soulsearching.domain.repository.FolderRepository
import com.github.enteraname74.soulsearching.domain.repository.MusicRepository

class SaveMusicsWithFoldersUseCase(
    private val musicRepository: MusicRepository,
    private val folderRepository: FolderRepository,
) {
    suspend operator fun invoke(musics: List<Music>) {
        musicRepository.upsertAll(musics)
        folderRepository.upsertAll(
            folders = musics.groupBy { it.folder }.map { (folderPath, _) ->
                Folder(
                    folderPath = folderPath,
                    isSelected = true,
                )
            }
        )
    }
}