package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.repository.AlbumRepository
import java.util.UUID

class UpdateNbPlayedOfAlbumUseCase(
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(
        albumId: UUID,
        newNbPlayed: Int,
    ) {
        albumRepository.updateNbPlayed(
            albumId = albumId,
            newNbPlayed = newNbPlayed,
        )
    }
}