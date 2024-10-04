package com.github.enteraname74.domain.usecase.albumartist

import com.github.enteraname74.domain.model.AlbumArtist
import com.github.enteraname74.domain.repository.AlbumArtistRepository

class UpsertAllAlbumArtistUseCase(
    private val albumArtistRepository: AlbumArtistRepository
) {
    suspend operator fun invoke(allAlbumArtists: List<AlbumArtist>) {
        albumArtistRepository.upsertAll(allAlbumArtists)
    }
}