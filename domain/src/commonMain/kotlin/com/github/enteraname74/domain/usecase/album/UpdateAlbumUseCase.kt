package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.util.CheckAndDeleteVerification

class UpdateAlbumUseCase(
    private val albumRepository: AlbumRepository,
    private val checkAndDeleteVerification: CheckAndDeleteVerification,
) {
    suspend operator fun invoke(
        newAlbumWithArtistInformation: AlbumWithArtist,
    ) {
        albumRepository.update(
            newAlbumWithArtistInformation = newAlbumWithArtistInformation,
        )
    }
}