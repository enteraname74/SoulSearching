package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.Flow

class GetAllAlbumsWithMusicsUseCase(
    private val albumRepository: AlbumRepository,
) {
    operator fun invoke(): Flow<List<AlbumWithMusics>> =
        albumRepository.getAllAlbumsWithMusics()
}