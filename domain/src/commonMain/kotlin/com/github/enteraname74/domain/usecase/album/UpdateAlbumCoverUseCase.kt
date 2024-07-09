package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.repository.AlbumRepository
import java.util.UUID

class UpdateAlbumCoverUseCase(
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(
        newCoverId: UUID,
        albumId: UUID,
    ) {
        albumRepository.updateAlbumCover(
            newCoverId = newCoverId,
            albumId = albumId,
        )
    }
}