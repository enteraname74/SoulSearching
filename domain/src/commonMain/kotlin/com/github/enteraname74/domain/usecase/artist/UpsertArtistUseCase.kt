package com.github.enteraname74.domain.usecase.artist

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.repository.ArtistRepository

class UpsertArtistUseCase(
    private val artistRepository: ArtistRepository,
) {
    suspend operator fun invoke(artist: Artist) {
        artistRepository.upsert(artist)
    }
}