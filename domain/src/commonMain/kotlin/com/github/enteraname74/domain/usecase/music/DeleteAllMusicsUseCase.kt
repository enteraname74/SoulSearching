package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.repository.MusicRepository
import java.util.UUID

class DeleteAllMusicsUseCase(
    private val musicRepository: MusicRepository,
) {
    suspend operator fun invoke(ids: List<UUID>) {
        musicRepository.deleteAll(
            ids = ids,
        )
    }
}