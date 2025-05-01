package com.github.enteraname74.domain.usecase.lyrics

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicLyrics
import com.github.enteraname74.domain.repository.LyricsRepository

class CommonLyricsUseCase(
    private val lyricsRepository: LyricsRepository,
) {
    suspend fun getLyricsForMusic(music: Music): MusicLyrics? =
        lyricsRepository.getLyricsOfSong(music = music)
}