package com.github.enteraname74.domain.usecase.musicartist

import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.domain.repository.MusicArtistRepository

class UpsertMusicIntoArtistUseCase(
    private val musicArtistRepository: MusicArtistRepository,
) {
    suspend operator fun invoke(musicArtist: MusicArtist) {
        musicArtistRepository.upsertMusicIntoArtist(musicArtist)
    }
}