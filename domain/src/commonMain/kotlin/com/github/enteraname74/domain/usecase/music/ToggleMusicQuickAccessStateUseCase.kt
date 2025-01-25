package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.MusicRepository

class ToggleMusicQuickAccessStateUseCase(
    private val musicRepository: MusicRepository,
) {
    suspend operator fun invoke(music: Music): SoulResult<String> =
        musicRepository.upsert(
            music = music.copy(
                isInQuickAccess = !music.isInQuickAccess,
            )
        )
}