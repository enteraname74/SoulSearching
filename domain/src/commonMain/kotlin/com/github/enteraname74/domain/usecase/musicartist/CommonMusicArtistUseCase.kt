package com.github.enteraname74.domain.usecase.musicartist

import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.domain.repository.MusicArtistRepository

class CommonMusicArtistUseCase(
    private val musicArtistRepository: MusicArtistRepository,
) {
    suspend fun upsertAll(allMusicArtists: List<MusicArtist>) {
        musicArtistRepository.upsertAll(allMusicArtists)
    }
}