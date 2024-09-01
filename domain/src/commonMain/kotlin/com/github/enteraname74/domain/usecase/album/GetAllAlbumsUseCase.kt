package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.Flow

class GetAllAlbumsUseCase(
    private val albumRepository: AlbumRepository,
) {
    operator fun invoke(): Flow<List<Album>> =
        albumRepository.getAll()
}