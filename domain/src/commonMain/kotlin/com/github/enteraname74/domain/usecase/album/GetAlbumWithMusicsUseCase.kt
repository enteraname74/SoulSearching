package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class GetAlbumWithMusicsUseCase(
    private val albumRepository: AlbumRepository,
) {
    operator fun invoke(albumId: UUID): Flow<AlbumWithMusics?> =
        albumRepository.getAlbumWithMusics(
            albumId = albumId,
        )
}