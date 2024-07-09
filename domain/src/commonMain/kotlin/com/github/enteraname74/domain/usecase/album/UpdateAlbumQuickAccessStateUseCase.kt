package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.repository.AlbumRepository
import java.util.UUID

class UpdateAlbumQuickAccessStateUseCase(
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(
        albumId: UUID,
        newQuickAccessState: Boolean,
    ) {
        albumRepository.updateQuickAccessState(
            albumId = albumId,
            newQuickAccessState = newQuickAccessState,
        )
    }
}