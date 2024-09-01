package com.github.enteraname74.domain.usecase.artist

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.repository.ArtistRepository
import kotlinx.coroutines.flow.first
import java.util.UUID

class UpdateArtistNbPlayedUseCase(
    private val artistRepository: ArtistRepository,
) {
    suspend operator fun invoke(artistId: UUID) {
        val artist: Artist = artistRepository.getFromId(artistId).first() ?: return
        artistRepository.upsert(
            artist = artist.copy(
                nbPlayed = artist.nbPlayed + 1,
            )
        )
    }
}