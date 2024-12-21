package com.github.enteraname74.domain.usecase.musicalbum

import com.github.enteraname74.domain.repository.MusicAlbumRepository
import java.util.*

class UpdateMusicsAlbumUseCase(
    private val musicAlbumRepository: MusicAlbumRepository
) {
    suspend operator fun invoke(
        newAlbumId: UUID,
        legacyAlbumId: UUID
    ) {
        musicAlbumRepository.updateMusicsAlbum(
            newAlbumId = newAlbumId,
            legacyAlbumId = legacyAlbumId,
        )
    }
}