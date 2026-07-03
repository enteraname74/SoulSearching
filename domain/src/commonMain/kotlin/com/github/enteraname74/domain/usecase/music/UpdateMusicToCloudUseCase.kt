package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.model.CloudMusic
import com.github.enteraname74.domain.model.MergeMode
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.repository.MusicRepository

class UpdateMusicToCloudUseCase(
    private val musicRepository: MusicRepository,
    private val upsertCloudMusicUseCase: UpsertCloudMusicUseCase,
) {
    suspend operator fun invoke(
        music: Music,
        mergeMode: MergeMode,
    ): Music? {
        val cloudMusic: CloudMusic = musicRepository.updateMusicToCloud(music = music) ?: return null
        return upsertCloudMusicUseCase(
            cloudMusic = cloudMusic,
            mergeMode = mergeMode,
        )
    }
}
