package com.github.enteraname74.domain.usecase.lyrics

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicLyrics
import com.github.enteraname74.domain.repository.LyricsRepository

class GetLyricsOfSongUseCase(
    private val lyricsRepository: LyricsRepository,
) {
    suspend operator fun invoke(music: Music): MusicLyrics? =
        lyricsRepository.getLyricsOfSong(music = music)
}