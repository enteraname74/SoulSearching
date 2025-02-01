package com.github.enteraname74.domain.usecase.artist

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.ArtistRepository

class ToggleArtistQuickAccessStateUseCase(
    private val artistRepository: ArtistRepository,
) {
    suspend operator fun invoke(artist: Artist): SoulResult<Unit> =
        artistRepository.upsert(
            artist = artist.copy(
                isInQuickAccess = !artist.isInQuickAccess,
            )
        )
}