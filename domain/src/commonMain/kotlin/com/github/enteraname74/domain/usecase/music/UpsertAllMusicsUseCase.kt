package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.repository.MusicRepository

class UpsertAllMusicsUseCase(
    private val musicRepository: MusicRepository,
) {
    suspend operator fun invoke(allMusics: List<Music>) {
        musicRepository.upsertAll(allMusics)
    }
}