package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.CloudMusic
import com.github.enteraname74.domain.model.MergeMode
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.repository.MusicRepository

class UploadMusicToCloudUseCase(
    private val musicRepository: MusicRepository,
    private val cloudMusicToMusicUseCase: CloudMusicToMusicUseCase,
) {
    suspend operator fun invoke(
        music: Music,
        mergeMode: MergeMode,
        cachedArtists: Set<Artist>,
        cachedAlbums: Set<Album>,
        cachedMusics: Set<Music>,
    ): Music? {
        val cloudMusic: CloudMusic =
            musicRepository.uploadMusicToCloud(music = music) ?: return null
        return cloudMusicToMusicUseCase(
            cloudMusic = cloudMusic,
            mergeMode = mergeMode,
            cachedArtists = cachedArtists,
            cachedAlbums = cachedAlbums,
            cachedMusics = cachedMusics,
        )
    }
}
