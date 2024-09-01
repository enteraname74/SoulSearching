package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.repository.MusicRepository
import kotlinx.coroutines.flow.first
import java.util.UUID

class IsMusicAlreadySavedUseCase(
    private val musicRepository: MusicRepository
) {
    suspend operator fun invoke(musicPath: String): Boolean =
        musicRepository.getFromPath(musicPath) != null

    suspend operator fun invoke(musicId: UUID): Boolean =
        musicRepository.getFromId(musicId).first() != null
}