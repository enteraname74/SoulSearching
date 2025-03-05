package com.github.enteraname74.domain.usecase.artist

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.repository.ArtistRepository

class GetArtistFromNameUseCase(
    private val artistRepository: ArtistRepository,
) {
    suspend operator fun invoke(artistName: String): Artist? =
        artistRepository.getFromName(artistName = artistName)
}