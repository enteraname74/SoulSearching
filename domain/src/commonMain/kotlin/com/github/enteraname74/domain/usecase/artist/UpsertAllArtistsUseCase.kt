package com.github.enteraname74.domain.usecase.artist

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.repository.ArtistRepository

class UpsertAllArtistsUseCase(
    private val artistRepository: ArtistRepository,
) {
    suspend operator fun invoke(allArtists: List<Artist>) {
        artistRepository.upsertAll(allArtists)
    }
}