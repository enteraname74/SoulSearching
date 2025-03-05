package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class GetMusicUseCase(
    private val musicRepository: MusicRepository,
) {
    operator fun invoke(musicId: UUID): Flow<Music?> =
        musicRepository.getFromId(musicId = musicId)
}