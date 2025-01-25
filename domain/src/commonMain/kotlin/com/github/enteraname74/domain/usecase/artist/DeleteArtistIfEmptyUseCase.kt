package com.github.enteraname74.domain.usecase.artist

import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.ArtistRepository
import kotlinx.coroutines.flow.first
import java.util.UUID

class DeleteArtistIfEmptyUseCase(
    private val artistRepository: ArtistRepository,
) {
    suspend operator fun invoke(
        artistId: UUID
    ): SoulResult<String> {
        val artistWithMusics: ArtistWithMusics =
            artistRepository.getArtistWithMusics(artistId = artistId).first() ?: return SoulResult.Success("")

        return if (artistWithMusics.musics.isEmpty()) {
            artistRepository.delete(artist = artistWithMusics.artist)
        } else {
            SoulResult.Success("")
        }
    }
}