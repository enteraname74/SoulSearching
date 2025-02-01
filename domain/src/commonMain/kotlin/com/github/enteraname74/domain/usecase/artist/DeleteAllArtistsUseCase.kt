package com.github.enteraname74.domain.usecase.artist

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.ArtistRepository
import java.util.UUID

class DeleteAllArtistsUseCase(
    private val artistRepository: ArtistRepository
) {
    suspend operator fun invoke(artistsIds: List<UUID>): SoulResult<Unit> =
        artistRepository.deleteAll(
            artistsIds = artistsIds,
        )
}