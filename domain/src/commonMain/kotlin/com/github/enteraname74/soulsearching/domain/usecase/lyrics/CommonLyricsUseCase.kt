package com.github.enteraname74.soulsearching.domain.usecase.lyrics

import com.github.enteraname74.soulsearching.domain.model.Music
import com.github.enteraname74.soulsearching.domain.model.lyrics.MusicLyrics
import com.github.enteraname74.soulsearching.domain.repository.LyricsRepository

class CommonLyricsUseCase(
    private val lyricsRepository: LyricsRepository,
) {
    suspend fun getLocalLyricsForMusic(music: Music): MusicLyrics? =
        lyricsRepository.getLocalLyricsOfSong(music = music)

    suspend fun getRemoteLyricsForMusic(music: Music): MusicLyrics? =
        lyricsRepository.getRemoteLyricsOfSong(music = music)
}
