package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.repository.AlbumRepository

class UpsertAlbumUseCase(
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(album: Album) {
        albumRepository.upsert(
            album = album,
        )
    }
}