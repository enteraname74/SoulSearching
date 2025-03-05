package com.github.enteraname74.domain.usecase.musicartist

import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.domain.repository.MusicArtistRepository

class UpsertAllMusicArtistsUseCase(
    private val musicArtistRepository: MusicArtistRepository,
) {
    suspend operator fun invoke(allMusicArtists: List<MusicArtist>) {
        musicArtistRepository.upsertAll(allMusicArtists)
    }
}