package com.github.enteraname74.domain.usecase.musicartist

import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.domain.repository.MusicArtistRepository
import java.util.UUID

class CommonMusicArtistUseCase(
    private val musicArtistRepository: MusicArtistRepository,
) {
    suspend fun upsertAll(allMusicArtists: List<MusicArtist>) {
        musicArtistRepository.upsertAll(allMusicArtists)
    }

    suspend fun deleteOfArtist(artistId: UUID) {
        musicArtistRepository.deleteOfArtist(artistId)
    }
}