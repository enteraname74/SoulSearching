package com.github.enteraname74.domain.usecase.musicartist

import com.github.enteraname74.domain.repository.MusicArtistRepository
import java.util.UUID

class GetArtistIdFromMusicIdUseCase(
    private val musicArtistRepository: MusicArtistRepository,
) {
    suspend operator fun invoke(musicId: UUID): UUID? =
        musicArtistRepository.getArtistIdFromMusicId(musicId)
}