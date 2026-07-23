package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.model.CloudMusic
import com.github.enteraname74.domain.model.MergeMode
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.MusicRepository

class FetchMusicFromUrlUseCase(
    private val musicRepository: MusicRepository,
    private val cloudMusicToMusicUseCase: CloudMusicToMusicUseCase,
) {
    suspend operator fun invoke(url: String): SoulResult<Unit> = SoulResult.runCatching {
        val cloudMusic: CloudMusic = musicRepository.fetch(url)
        val builtMusic: Music = cloudMusicToMusicUseCase(
            cloudMusic = cloudMusic,
            mergeMode = MergeMode.LocalFirst,
            cachedArtists = emptySet(),
            cachedAlbums = emptySet(),
            cachedMusics = emptySet(),
        )
        musicRepository.upsert(builtMusic)
    }
}
