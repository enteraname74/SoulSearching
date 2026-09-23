package com.github.enteraname74.soulsearching.domain.usecase.music

import com.github.enteraname74.soulsearching.domain.model.Music
import com.github.enteraname74.soulsearching.domain.repository.MusicRepository

class RemoveLocallyOrDeleteMusicUseCase(
    private val deleteMusicUseCase: DeleteMusicUseCase,
    private val musicRepository: MusicRepository,
) {
    suspend operator fun invoke(music: Music): Boolean =
        if (music.remoteId != null) {
            musicRepository.upsert(
                music.copy(
                    localPath = null,
                )
            )
            false
        } else {
            deleteMusicUseCase(music)
            true
        }
}
