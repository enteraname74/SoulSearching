package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.Flow

class GetAllAlbumsWithArtistUseCase(
    private val albumRepository: AlbumRepository
) {
    operator fun invoke(): Flow<List<AlbumWithArtist>> =
        albumRepository.getAllAlbumsWithArtist()
}