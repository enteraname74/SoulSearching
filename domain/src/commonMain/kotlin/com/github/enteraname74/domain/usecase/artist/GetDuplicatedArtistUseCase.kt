package com.github.enteraname74.domain.usecase.artist

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.repository.ArtistRepository
import kotlinx.coroutines.flow.first
import java.util.*

class GetDuplicatedArtistUseCase(
    private val artistRepository: ArtistRepository,
) {
    suspend operator fun invoke(
        artistId: UUID,
        artistName: String
    ): ArtistWithMusics? {
        val allArtists: List<ArtistWithMusics> = artistRepository.getAllArtistWithMusics().first()

        return allArtists
            .firstOrNull {
                it.artist.artistName == artistName && it.artist.artistId != artistId
            }
    }
}