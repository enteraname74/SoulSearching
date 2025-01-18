package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.repository.MusicRepository
import java.util.*

class UpdateMusicsAlbumUseCase(
    private val musicRepository: MusicRepository,
) {
    suspend operator fun invoke(
        newAlbumId: UUID,
        legacyAlbumId: UUID
    ) {
        musicRepository.updateMusicsAlbum(
            newAlbumId = newAlbumId,
            legacyAlbumId = legacyAlbumId,
        )
    }
}