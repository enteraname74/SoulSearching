package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.repository.MusicRepository
import kotlinx.coroutines.flow.first
import java.util.UUID

class UpdateMusicNbPlayedUseCase(
    private val musicRepository: MusicRepository,
) {
    suspend operator fun invoke(musicId: UUID) {
        val music: Music = musicRepository.getFromId(musicId).first() ?: return
        musicRepository.upsert(
            music = music.copy(
                nbPlayed = music.nbPlayed + 1
            ),
        )
    }
}