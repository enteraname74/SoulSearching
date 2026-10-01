package com.github.enteraname74.soulsearching.domain.usecase

import com.github.enteraname74.soulsearching.domain.repository.AlbumRepository
import com.github.enteraname74.soulsearching.domain.repository.ArtistRepository

class DeleteEmptyAlbumsAndArtistsUseCase(
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
) {
    suspend operator fun invoke() {
        albumRepository.deleteAllEmpty()
        artistRepository.deleteAllEmpty()
    }
}
