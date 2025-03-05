package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.repository.AlbumRepository

class UpsertAllAlbumsUseCase(
    private val albumRepository: AlbumRepository
) {
    suspend operator fun invoke(albums: List<Album>) {
        albumRepository.upsertAll(albums)
    }
}