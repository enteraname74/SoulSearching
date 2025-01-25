package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.AlbumRepository
import java.util.UUID

class DeleteAllAlbumsUseCase(
    private val albumRepository: AlbumRepository
) {
    suspend operator fun invoke(albumsIds: List<UUID>): SoulResult<String> =
        albumRepository.deleteAll(
            ids = albumsIds,
        )
}