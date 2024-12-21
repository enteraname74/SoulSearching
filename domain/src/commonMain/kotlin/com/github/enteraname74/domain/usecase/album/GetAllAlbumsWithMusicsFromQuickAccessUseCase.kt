package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllAlbumsWithMusicsFromQuickAccessUseCase(
    private val albumRepository: AlbumRepository,
) {
    operator fun invoke(): Flow<List<AlbumWithMusics>> =
        albumRepository.getAllAlbumWithMusics().map { list ->
            list.filter { it.album.isInQuickAccess }
        }
}