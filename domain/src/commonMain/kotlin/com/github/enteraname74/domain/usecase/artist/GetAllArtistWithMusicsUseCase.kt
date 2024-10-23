package com.github.enteraname74.domain.usecase.artist

import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.repository.ArtistRepository
import kotlinx.coroutines.flow.Flow

class GetAllArtistWithMusicsUseCase(
    private val artistRepository: ArtistRepository
) {
    operator fun invoke(): Flow<List<ArtistWithMusics>> =
        artistRepository.getAllArtistWithMusics()
}