package com.github.enteraname74.domain.usecase.artist

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.repository.ArtistRepository
import kotlinx.coroutines.flow.Flow

class GetAllArtistsUseCase(
    private val artistRepository: ArtistRepository,
) {
    operator fun invoke(): Flow<List<Artist>> =
        artistRepository.getAll()
}