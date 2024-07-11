package com.github.enteraname74.domain.usecase.artist

import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.repository.ArtistRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class GetArtistWithMusicsUseCase(
    private val artistRepository: ArtistRepository,
) {
    operator fun invoke(artistId: UUID): Flow<ArtistWithMusics?> =
        artistRepository.getArtistWithMusics(
            artistId = artistId,
        )
}