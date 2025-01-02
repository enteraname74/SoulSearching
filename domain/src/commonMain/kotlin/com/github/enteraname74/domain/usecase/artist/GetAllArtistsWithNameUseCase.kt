package com.github.enteraname74.domain.usecase.artist

import com.github.enteraname74.domain.repository.ArtistRepository

class GetAllArtistsWithNameUseCase(
    private val artistRepository: ArtistRepository
) {
    suspend operator fun invoke(artistsNames: List<String>) =
        artistRepository.getAllFromName(artistsNames)
}