package com.github.enteraname74.domain.usecase.artist

import com.github.enteraname74.domain.repository.ArtistRepository
import kotlinx.coroutines.flow.first

class GetArtistsNameFromSearchStringUseCase(
    private val artistRepository: ArtistRepository,
) {
    suspend operator fun invoke(searchString: String): List<String> {
        if (searchString.isBlank()) return emptyList()

        val allArtists = artistRepository.getAll().first()

        return allArtists.filter { it.artistName.contains(searchString) }.map { it.artistName }
    }
}