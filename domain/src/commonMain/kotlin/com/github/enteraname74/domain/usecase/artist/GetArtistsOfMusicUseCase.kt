package com.github.enteraname74.domain.usecase.artist

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.repository.ArtistRepository
import kotlinx.coroutines.flow.Flow
import java.util.*

class GetArtistsOfMusicUseCase(
    private val artistRepository: ArtistRepository,
) {
    operator fun invoke(musicId: UUID): Flow<List<Artist>> =
        artistRepository.getArtistsOfMusic(musicId)
}