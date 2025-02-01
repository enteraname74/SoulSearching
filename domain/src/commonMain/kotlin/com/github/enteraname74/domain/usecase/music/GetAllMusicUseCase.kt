package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow

class GetAllMusicUseCase(
    private val musicRepository: MusicRepository,
) {
    operator fun invoke(
        dataMode: DataMode? = null,
    ): Flow<List<Music>> =
        musicRepository.getAll(dataMode)
}