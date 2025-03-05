package com.github.enteraname74.domain.usecase.albumartist

import com.github.enteraname74.domain.model.AlbumArtist
import com.github.enteraname74.domain.repository.AlbumArtistRepository

class UpsertAlbumArtistUseCase(
    private val albumArtistRepository: AlbumArtistRepository
) {
    suspend operator fun invoke(
        albumArtist: AlbumArtist,
    ) {
        albumArtistRepository.upsert(
            albumArtist = albumArtist,
        )
    }
}