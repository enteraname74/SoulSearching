package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.repository.MusicRepository
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class GetAllMusicFromFolderPathUseCase(
    private val musicRepository: MusicRepository,
) {
    operator fun invoke(folderPath: String) =
        musicRepository.getAll().map { list ->
            list.filter {
                it.folder == folderPath
            }
        }
}