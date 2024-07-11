package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllMusicFromQuickAccessUseCase(
    private val musicRepository: MusicRepository,
) {
    operator fun invoke(): Flow<List<Music>> =
        musicRepository.getAll().map { list ->
            list.filter { it.isInQuickAccess }
        }
}