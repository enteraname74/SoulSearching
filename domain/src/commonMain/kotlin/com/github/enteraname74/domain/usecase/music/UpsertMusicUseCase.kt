package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.repository.MusicRepository

class UpsertMusicUseCase(
    private val musicRepository: MusicRepository,
) {
    suspend operator fun invoke(music: Music) {
        musicRepository.upsert(music = music)
    }
}