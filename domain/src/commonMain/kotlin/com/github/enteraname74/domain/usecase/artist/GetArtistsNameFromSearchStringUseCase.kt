package com.github.enteraname74.domain.usecase.artist

import com.github.enteraname74.domain.repository.ArtistRepository

class GetArtistsNameFromSearchStringUseCase(
    private val artistRepository: ArtistRepository,
) {
    suspend operator fun invoke(searchString: String): List<String> =
        if (searchString.isBlank()) {
            emptyList()
        } else {
            artistRepository.getArtistNamesContainingSearch(searchString)
        }
}