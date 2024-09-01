package com.github.enteraname74.domain.usecase.artist

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import kotlinx.coroutines.flow.first
import java.util.UUID

class GetCorrespondingArtistUseCase(
    private val musicArtistRepository: MusicArtistRepository,
    private val artistRepository: ArtistRepository,
) {
    suspend operator fun invoke(musicId: UUID): Artist? {
        val artistId = musicArtistRepository.getArtistIdFromMusicId(
            musicId = musicId
        ) ?: return null
        return artistRepository.getFromId(
            artistId = artistId
        ).first()
    }
}