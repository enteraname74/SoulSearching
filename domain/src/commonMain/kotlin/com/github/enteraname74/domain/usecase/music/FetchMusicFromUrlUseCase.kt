package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.model.CloudMusic
import com.github.enteraname74.domain.model.MergeMode
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.MusicRepository

class FetchMusicFromUrlUseCase(
    private val musicRepository: MusicRepository,
    private val upsertCloudMusicUseCase: UpsertCloudMusicUseCase,
) {
    suspend operator fun invoke(url: String): SoulResult<Unit> = SoulResult.runCatching {
        val cloudMusic: CloudMusic = musicRepository.fetch(url)
        upsertCloudMusicUseCase(
            cloudMusic = cloudMusic,
            mergeMode = MergeMode.LocalFirst,
        )
    }
}
